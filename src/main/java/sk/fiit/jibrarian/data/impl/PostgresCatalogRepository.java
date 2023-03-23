package sk.fiit.jibrarian.data.impl;

import sk.fiit.jibrarian.data.CatalogRepository;
import sk.fiit.jibrarian.data.ConnectionPool;
import sk.fiit.jibrarian.model.BorrowedItem;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.ItemType;
import sk.fiit.jibrarian.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgresCatalogRepository implements CatalogRepository {
    private static final Logger LOGGER = Logger.getLogger(PostgresCatalogRepository.class.getName());
    private final ConnectionPool connectionPool;

    public PostgresCatalogRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void saveItem(Item item) throws ItemAlreadyExistsException {
        try (
                var connWrapper = connectionPool.getConnWrapper();
                var statement = connWrapper.getConnection().prepareStatement(
                        """
                                insert into items (id, title, author, description, language, genre, isbn, item_type, pages, total, available,
                                                   reserved, image)
                                values (?, ?, ?, ?, ?, ?, ?, ?::item_type, ?, ?, ?, ?, ?);
                                     """)

        ) {
            statement.setObject(1, item.getId());
            statement.setString(2, item.getTitle());
            statement.setString(3, item.getAuthor());
            statement.setString(4, item.getDescription());
            statement.setString(5, item.getLanguage());
            statement.setString(6, item.getGenre());
            statement.setString(7, item.getIsbn());
            statement.setString(8, item.getItemType().getDbValue());
            statement.setInt(9, item.getPages());
            statement.setInt(10, item.getTotal());
            statement.setInt(11, item.getAvailable());
            statement.setInt(12, item.getReserved());
            statement.setBytes(13, item.getImage());
            statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                LOGGER.log(Level.WARNING, "Item {0} already exists", item.getId());
                throw new ItemAlreadyExistsException(String.format("Item with id %s already exists", item.getId()));
            }
            LOGGER.log(Level.SEVERE, "Error while saving item", e);
        }
    }

    @Override
    public List<Item> getItemPage(Integer page, Integer pageSize) {
        try (
                var connWrapper = connectionPool.getConnWrapper();
                var statement = connWrapper.getConnection().prepareStatement(
                        """
                                select *
                                from items
                                order by title
                                limit ? offset ?;
                                     """)
        ) {
            statement.setInt(1, pageSize);
            statement.setInt(2, page * pageSize);
            var resultSet = statement.executeQuery();
            var items = new ArrayList<Item>();
            while (resultSet.next()) {
                var item = readItem(resultSet);
                items.add(item);
            }
            return items;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while getting item page", e);
        }
        return Collections.emptyList();
    }

    @Override
    public void updateItem(Item item) throws ItemNotFoundException {
        try (
                var connWrapper = connectionPool.getConnWrapper();
                var statement = connWrapper.getConnection().prepareStatement(
                        """
                                update items
                                set title = ?, author = ?, description = ?, language = ?, genre = ?, isbn = ?, item_type = ?::item_type,
                                    pages = ?, total = ?, available = ?, reserved = ?, image = ?
                                where id = ?;
                                     """)
        ) {
            statement.setString(1, item.getTitle());
            statement.setString(2, item.getAuthor());
            statement.setString(3, item.getDescription());
            statement.setString(4, item.getLanguage());
            statement.setString(5, item.getGenre());
            statement.setString(6, item.getIsbn());
            statement.setString(7, item.getItemType().getDbValue());
            statement.setInt(8, item.getPages());
            statement.setInt(9, item.getTotal());
            statement.setInt(10, item.getAvailable());
            statement.setInt(11, item.getReserved());
            statement.setBytes(12, item.getImage());
            statement.setObject(13, item.getId());
            var updated = statement.executeUpdate();
            if (updated == 0) {
                throw new ItemNotFoundException(String.format("Item with id %s not found", item.getId()));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while updating item", e);
        }
    }

    @Override
    public BorrowedItem lendItem(Item item, User user, LocalDate until) throws ItemNotAvailableException {
        try (
                var connWrapper = connectionPool.getConnWrapper();
                var insertBorrowedItem = connWrapper.getConnection().prepareStatement(
                        """
                                insert into borrowed_items (id, item_id, user_id, until)
                                values (?, ?, ?, ?);
                                     """);
                var updateItem = connWrapper.getConnection().prepareStatement(
                        """
                                update items
                                set available = available - 1, reserved = reserved + 1
                                where id = ?;
                                     """)
        ) {
            connWrapper.getConnection().setAutoCommit(false);
            var borrowedItem = new BorrowedItem();
            borrowedItem.setId(UUID.randomUUID());
            borrowedItem.setItemId(item.getId());
            borrowedItem.setUserId(user.getId());
            borrowedItem.setUntil(until);
            borrowedItem.setTitle(item.getTitle());
            borrowedItem.setAuthor(item.getAuthor());
            insertBorrowedItem.setObject(1, borrowedItem.getId());
            insertBorrowedItem.setObject(2, borrowedItem.getItemId());
            insertBorrowedItem.setObject(3, borrowedItem.getUserId());
            insertBorrowedItem.setObject(4, until);
            executeUpdateOrRollback(connWrapper.getConnection(), insertBorrowedItem);

            updateItem.setObject(1, item.getId());
            executeUpdateOrRollback(connWrapper.getConnection(), updateItem);
            connWrapper.getConnection().commit();
            connWrapper.getConnection().setAutoCommit(true);
            return borrowedItem;
        } catch (SQLException e) {
            if ("23514".equals(e.getSQLState())) {
                LOGGER.log(Level.WARNING, "Item {0} is not available", item.getId());
                throw new ItemNotAvailableException(String.format("Item with id %s is not available", item.getId()));
            }
            LOGGER.log(Level.SEVERE, "Error while lending item", e);
        }
        return null;
    }

    @Override
    public List<BorrowedItem> getBorrowedItemsForUser(User user) {
        try (
                var connWrapper = connectionPool.getConnWrapper();
                var statement = connWrapper.getConnection().prepareStatement(
                        """
                                select
                                    bi.id,
                                    bi.item_id,
                                    bi.user_id,
                                    bi.until,
                                    i.title,
                                    i.author
                                from borrowed_items bi
                                         join items i on bi.item_id = i.id
                                where user_id = ?
                                 """)
        ) {
            var borrowedItems = new ArrayList<BorrowedItem>();
            statement.setObject(1, user.getId());
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                var borrowedItem = new BorrowedItem();
                borrowedItem.setId(resultSet.getObject("id", UUID.class));
                borrowedItem.setItemId(resultSet.getObject("item_id", UUID.class));
                borrowedItem.setUserId(resultSet.getObject("user_id", UUID.class));
                borrowedItem.setUntil(resultSet.getObject("until", LocalDate.class));
                borrowedItem.setTitle(resultSet.getString("title"));
                borrowedItem.setAuthor(resultSet.getString("author"));
                borrowedItems.add(borrowedItem);
            }
            return borrowedItems;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while getting borrowed items for user", e);
        }
        return Collections.emptyList();
    }

    @Override
    public Item returnItem(BorrowedItem borrowedItem) throws ItemNotFoundException {
        try (
                var connWrapper = connectionPool.getConnWrapper();
                var setAsDeleted = connWrapper.getConnection().prepareStatement(
                        """
                                update borrowed_items
                                set deleted_at = now()
                                where id = ?;
                                     """);
                var updateItem = connWrapper.getConnection().prepareStatement(
                        """
                                update items
                                set available = available + 1,
                                    reserved  = reserved - 1
                                where id = ?
                                returning id, title, author, description, language, genre, isbn, item_type,
                                    pages, total, available, reserved, image;
                                     """)

        ) {
            connWrapper.getConnection().setAutoCommit(false);
            setAsDeleted.setObject(1, borrowedItem.getId());
            executeUpdateOrRollback(connWrapper.getConnection(), setAsDeleted);

            updateItem.setObject(1, borrowedItem.getItemId());
            var resultSet = executeOrRollback(connWrapper.getConnection(), updateItem);
            if (resultSet.next()) {
                Item item = readItem(resultSet);
                connWrapper.getConnection().commit();
                connWrapper.getConnection().setAutoCommit(true);
                return item;
            }
            LOGGER.log(Level.WARNING, "Item {0} not found", borrowedItem.getItemId());
            throw new ItemNotFoundException(String.format("Item with id %s not found", borrowedItem.getItemId()));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while returning item", e);
        }
        return null;
    }

    private Item readItem(ResultSet resultSet) throws SQLException {
        var item = new Item();
        item.setId(resultSet.getObject("id", UUID.class));
        item.setTitle(resultSet.getString("title"));
        item.setAuthor(resultSet.getString("author"));
        item.setDescription(resultSet.getString("description"));
        item.setLanguage(resultSet.getString("language"));
        item.setGenre(resultSet.getString("genre"));
        item.setIsbn(resultSet.getString("isbn"));
        item.setItemType(ItemType.fromDbValue(resultSet.getString("item_type")));
        item.setPages(resultSet.getInt("pages"));
        item.setTotal(resultSet.getInt("total"));
        item.setAvailable(resultSet.getInt("available"));
        item.setReserved(resultSet.getInt("reserved"));
        item.setImage(resultSet.getBytes("image"));
        return item;
    }

    private void executeUpdateOrRollback(Connection connection, PreparedStatement statement) throws SQLException {
        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    private ResultSet executeOrRollback(Connection connection, PreparedStatement statement) throws SQLException {
        try {
            return statement.executeQuery();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }
}
