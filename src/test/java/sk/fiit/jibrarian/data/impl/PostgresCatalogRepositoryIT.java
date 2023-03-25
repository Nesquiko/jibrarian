package sk.fiit.jibrarian.data.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.fiit.jibrarian.data.CatalogRepository.ItemAlreadyExistsException;
import sk.fiit.jibrarian.data.CatalogRepository.ItemNotAvailableException;
import sk.fiit.jibrarian.data.CatalogRepository.ItemNotFoundException;
import sk.fiit.jibrarian.data.ConnectionPool;
import sk.fiit.jibrarian.data.ConnectionPool.ConnectionPoolBuilder;
import sk.fiit.jibrarian.model.BorrowedItem;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.ItemType;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PostgresCatalogRepositoryIT {

    private static ConnectionPool connectionPool;
    private PostgresCatalogRepository postgresCatalogRepository;

    private static byte[] image;
    private Item item;
    private User user;

    @BeforeAll
    static void setUpClass() throws SQLException, IOException, URISyntaxException {
        URI uri = Objects.requireNonNull(PostgresCatalogRepositoryIT.class.getResource("/hitchhikers-guide-cover.png"))
                .toURI();
        Path path = Path.of(uri);
        image = Files.readAllBytes(path);
        connectionPool = new ConnectionPoolBuilder()
                .setHost("localhost")
                .setPort(42069)
                .setDatabase("jibrarian")
                .setUser("jibrarian")
                .setPassword("password")
                .setInitialPoolSize(2)
                .build();
    }

    @AfterAll
    static void tearDownClass() throws SQLException {
        clearDatabase();
        connectionPool.close();
    }

    @BeforeEach
    void setUp() {
        postgresCatalogRepository = new PostgresCatalogRepository(connectionPool);
        clearDatabase();
        item = createItem();
        user = new User(UUID.randomUUID(), "email", "passHash", Role.MEMBER);
        saveUser(user);
    }

    @Test
    void saveItemAlreadyExists() throws ItemAlreadyExistsException {
        postgresCatalogRepository.saveItem(item);
        assertThrows(ItemAlreadyExistsException.class, () -> postgresCatalogRepository.saveItem(item));
    }

    @Test
    void saveItem() throws ItemAlreadyExistsException {
        postgresCatalogRepository.saveItem(item);
        var items = postgresCatalogRepository.getItemPage(0, 10);
        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
        assertArrayEquals(image, items.get(0).getImage());
    }

    @Test
    void getItemPageCorrectPaging() throws ItemAlreadyExistsException {
        for (int i = 0; i < 20; i++) {
            postgresCatalogRepository.saveItem(createItem());
        }
        var items = postgresCatalogRepository.getItemPage(0, 9);
        assertEquals(9, items.size());
        items = postgresCatalogRepository.getItemPage(1, 9);
        assertEquals(9, items.size());
    }

    @Test
    void updateItemNotFound() {
        assertThrows(ItemNotFoundException.class, () -> postgresCatalogRepository.updateItem(item));
    }

    @Test
    void updateItem() throws ItemAlreadyExistsException, ItemNotFoundException {
        postgresCatalogRepository.saveItem(item);
        item.setTitle("new title");
        postgresCatalogRepository.updateItem(item);
        var items = postgresCatalogRepository.getItemPage(0, 10);
        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
    }

    @Test
    void lendItemNotAvailable() throws ItemAlreadyExistsException {
        item.setAvailable(0);
        item.setTotal(0);
        postgresCatalogRepository.saveItem(item);
        assertThrows(ItemNotAvailableException.class,
                () -> postgresCatalogRepository.lendItem(item, user, LocalDate.now().plusDays(1)));
    }

    @Test
    void lendItem() throws ItemAlreadyExistsException, ItemNotAvailableException {
        item.setTotal(1);
        item.setAvailable(1);
        postgresCatalogRepository.saveItem(item);
        var borrowedItem = postgresCatalogRepository.lendItem(item, user, LocalDate.now().plusDays(1));
        assertEquals(item.getTitle(), borrowedItem.getTitle());
        assertEquals(item.getAuthor(), borrowedItem.getAuthor());

        var items = postgresCatalogRepository.getItemPage(0, 10);
        assertEquals(1, items.size());
        assertEquals(0, items.get(0).getAvailable());
        assertEquals(1, items.get(0).getTotal());
        assertEquals(1, items.get(0).getReserved());
    }

    @Test
    void getBorrowedItemsForUser() throws ItemAlreadyExistsException, ItemNotAvailableException {
        postgresCatalogRepository.saveItem(item);
        var borrowedItem = postgresCatalogRepository.lendItem(item, user, LocalDate.now().plusDays(1));
        var items = postgresCatalogRepository.getBorrowedItemsForUser(user);
        assertEquals(1, items.size());
        assertEquals(borrowedItem, items.get(0));
        assertEquals(item.getTitle(), items.get(0).getTitle());
        assertEquals(item.getAuthor(), items.get(0).getAuthor());
    }

    @Test
    void returnItem() throws ItemAlreadyExistsException, ItemNotAvailableException, ItemNotFoundException {
        postgresCatalogRepository.saveItem(item);
        var borrowedItem = postgresCatalogRepository.lendItem(item, user, LocalDate.now().plusDays(1));
        var returnedItem = postgresCatalogRepository.returnItem(borrowedItem);
        assertEquals(item.getTitle(), returnedItem.getTitle());
        assertEquals(item.getAuthor(), returnedItem.getAuthor());
        assertEquals(item.getAvailable(), returnedItem.getAvailable());
        assertEquals(item.getTotal(), returnedItem.getTotal());
        assertEquals(item.getReserved(), returnedItem.getReserved());
    }

    @Test
    void returnItemNotFound() {
        var borrowedItem = new BorrowedItem();
        borrowedItem.setId(UUID.randomUUID());
        assertThrows(ItemNotFoundException.class, () -> postgresCatalogRepository.returnItem(borrowedItem));
    }

    private static Item createItem() {
        return new Item(UUID.randomUUID(), "title", "author", "description", "language", "genre", "isbn", ItemType.BOOK,
                100, 10, 10, 0, image);
    }

    private static void saveUser(User user) {
        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection()
                        .prepareStatement(
                                "insert into users (id, email, pass_hash, role) values (?, ?, ?, ?::user_role)")
        ) {
            statement.setObject(1, user.getId());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassHash());
            statement.setString(4, user.getRole().getDbValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void clearDatabase() {
        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection()
                        .prepareStatement("""
                                truncate table borrowed_items;
                                truncate table items cascade;
                                truncate table users cascade;
                                """)
        ) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}