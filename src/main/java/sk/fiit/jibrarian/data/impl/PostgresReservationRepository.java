package sk.fiit.jibrarian.data.impl;

import sk.fiit.jibrarian.data.CatalogRepository.ItemNotAvailableException;
import sk.fiit.jibrarian.data.ConnectionPool;
import sk.fiit.jibrarian.data.ReservationRepository;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.ItemType;
import sk.fiit.jibrarian.model.Reservation;
import sk.fiit.jibrarian.model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgresReservationRepository extends DbTxHandler implements ReservationRepository {
    private static final Logger LOGGER = Logger.getLogger(PostgresReservationRepository.class.getName());
    private final ConnectionPool connectionPool;

    public PostgresReservationRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Item saveReservation(Reservation reservation)
            throws TooManyReservationsException, ItemNotAvailableException {
        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection().prepareStatement(
                        "select count(*) from reservations where user_id = ? and deleted_at is null")
        ) {
            statement.setObject(1, reservation.getUserId());
            var resultSet = statement.executeQuery();
            if (resultSet.next() && (resultSet.getInt(1) >= 3)) {
                LOGGER.log(Level.WARNING, "User {0} has too many reservations", reservation.getUserId());
                throw new TooManyReservationsException(
                        String.format("User %s has too many reservations", reservation.getUserId()));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while checking reservations for user", e);
            return reservation.getItem();
        }

        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection().prepareStatement(
                        "select available from items where id = ?")
        ) {
            statement.setObject(1, reservation.getItem().getId());
            var resultSet = statement.executeQuery();
            if (resultSet.next() && (resultSet.getInt(1) <= 0)) {
                LOGGER.log(Level.WARNING, "Item {0} is not available", reservation.getItem().getId());
                throw new ItemNotAvailableException(
                        String.format("Item %s is not available", reservation.getItem().getId()));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while checking availability of item", e);
            return reservation.getItem();
        }

        try (
                var connWrapper = connectionPool.getConnWrapper();
                var itemUpdate = connWrapper.getConnection().prepareStatement(
                        "update items set available = available - 1, reserved = reserved + 1 where id = ?");
                var reservationInsert = connWrapper.getConnection().prepareStatement(
                        "insert into reservations (id, user_id, item_id, until, deleted_at) values (?, ?, ?, ?, ?)")
        ) {

            connWrapper.getConnection().setAutoCommit(false);
            itemUpdate.setObject(1, reservation.getItem().getId());
            executeUpdateOrRollback(connWrapper.getConnection(), itemUpdate);

            reservationInsert.setObject(1, reservation.getId());
            reservationInsert.setObject(2, reservation.getUserId());
            reservationInsert.setObject(3, reservation.getItem().getId());
            reservationInsert.setObject(4, reservation.getUntil());
            reservationInsert.setObject(5, reservation.getDeletedAt());
            executeUpdateOrRollback(connWrapper.getConnection(), reservationInsert);

            connWrapper.getConnection().commit();
            connWrapper.getConnection().setAutoCommit(true);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while saving reservation", e);
            return reservation.getItem();
        }

        var item = reservation.getItem();
        var newReserved = item.getReserved() + 1;
        var newAvailable = item.getAvailable() - 1;
        return newUpdatedItem(item, newReserved, newAvailable);
    }

    @Override
    public List<Reservation> getReservationsForUser(User user) {
        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection().prepareStatement(
                        """
                                select
                                    r.id,
                                    r.user_id,
                                    until,
                                    deleted_at,
                                    i.id,
                                    i.title,
                                    i.author,
                                    i.description,
                                    i.language,
                                    i.genre,
                                    i.isbn,
                                    i.item_type,
                                    i.pages,
                                    i.total,
                                    i.available,
                                    i.reserved,
                                    i.image
                                from reservations r
                                         join items i on r.item_id = i.id
                                where user_id = ?
                                  and deleted_at is null
                                """)
        ) {
            statement.setObject(1, user.getId());
            var resultSet = statement.executeQuery();
            var reservations = new ArrayList<Reservation>();
            while (resultSet.next()) {
                var reservation = new Reservation();
                var item = new Item();
                reservation.setId(resultSet.getObject(1, UUID.class));
                reservation.setUserId(resultSet.getObject(2, UUID.class));
                reservation.setUntil(resultSet.getObject(3, LocalDate.class));
                reservation.setDeletedAt(resultSet.getObject(4, LocalDateTime.class));
                item.setId(resultSet.getObject(5, UUID.class));
                item.setTitle(resultSet.getString(6));
                item.setAuthor(resultSet.getString(7));
                item.setDescription(resultSet.getString(8));
                item.setLanguage(resultSet.getString(9));
                item.setGenre(resultSet.getString(10));
                item.setIsbn(resultSet.getString(11));
                item.setItemType(ItemType.fromDbValue(resultSet.getString(12)));
                item.setPages(resultSet.getInt(13));
                item.setTotal(resultSet.getInt(14));
                item.setAvailable(resultSet.getInt(15));
                item.setReserved(resultSet.getInt(16));
                item.setImage(resultSet.getBytes(17));

                reservation.setItem(item);
                reservations.add(reservation);
            }
            return reservations;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while getting reservations for user", e);
        }
        return Collections.emptyList();
    }

    @Override
    public void deleteReservation(Reservation reservation) {
        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection().prepareStatement(
                        "update reservations set deleted_at = ? where id = ?")
        ) {
            statement.setObject(1, LocalDateTime.now());
            statement.setObject(2, reservation.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while deleting reservation", e);
        }
    }


    private Item newUpdatedItem(Item item, int newReserved, int newAvailable) {
        return new Item(
                item.getId(),
                item.getTitle(),
                item.getAuthor(),
                item.getDescription(),
                item.getLanguage(),
                item.getGenre(),
                item.getIsbn(),
                item.getItemType(),
                item.getPages(),
                item.getTotal(),
                newAvailable,
                newReserved,
                item.getImage()
        );
    }
}
