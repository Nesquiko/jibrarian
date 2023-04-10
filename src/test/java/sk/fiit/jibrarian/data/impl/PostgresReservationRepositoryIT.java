package sk.fiit.jibrarian.data.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.fiit.jibrarian.data.CatalogRepository.ItemNotAvailableException;
import sk.fiit.jibrarian.data.ConnectionPool;
import sk.fiit.jibrarian.data.ConnectionPool.ConnectionPoolBuilder;
import sk.fiit.jibrarian.data.ReservationRepository.ItemAlreadyReservedException;
import sk.fiit.jibrarian.data.ReservationRepository.TooManyReservationsException;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.ItemType;
import sk.fiit.jibrarian.model.Reservation;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PostgresReservationRepositoryIT {

    private static ConnectionPool connectionPool;
    private PostgresReservationRepository postgresReservationRepository;
    private User user;
    private Item item;

    @BeforeAll
    static void setUpClass() throws SQLException {
        connectionPool = new ConnectionPoolBuilder()
                .setHost("localhost")
                .setPort(42069)
                .setDatabase("jibrarian")
                .setUser("jibrarian")
                .setPassword("password")
                .build();
    }

    @AfterAll
    static void tearDownClass() throws SQLException {
        clearDatabase();
        connectionPool.close();
    }

    @BeforeEach
    void setUp() {
        postgresReservationRepository = new PostgresReservationRepository(connectionPool);
        clearDatabase();
        user = new User(UUID.randomUUID(), "email", "passHash", Role.MEMBER);
        item = new Item(UUID.randomUUID(), "title", "author", "description", "language", "genre", "isbn", ItemType.BOOK,
                100, 10, 10, 0, null);
        saveUser(user);
        saveItem(item);
    }

    @Test
    void saveReservation() throws TooManyReservationsException, ItemNotAvailableException,
            ItemAlreadyReservedException {
        var reservation = new Reservation(UUID.randomUUID(), user.getId(), item,
                LocalDate.now().plusDays(1), null);
        var savedItem = postgresReservationRepository.saveReservation(reservation);
        var reservations = postgresReservationRepository.getReservationsForUser(user);
        assertEquals(1, reservations.size());
        assertEquals(9, savedItem.getAvailable());
        assertEquals(1, savedItem.getReserved());
    }

    @Test
    void saveReservationTooManyReservations()
            throws TooManyReservationsException, ItemNotAvailableException, ItemAlreadyReservedException {
        var reservation = newReservation();
        postgresReservationRepository.saveReservation(reservation);
        reservation = newReservationWithNewItem();
        postgresReservationRepository.saveReservation(reservation);
        reservation = newReservationWithNewItem();
        postgresReservationRepository.saveReservation(reservation);
        assertThrows(TooManyReservationsException.class,
                () -> postgresReservationRepository.saveReservation(newReservation()));
    }

    @Test
    void saveReservationItemNotAvailable() {
        item.setAvailable(0);
        item.setReserved(10);
        updateItemCounts(item);

        assertThrows(ItemNotAvailableException.class,
                () -> postgresReservationRepository.saveReservation(newReservation()));
    }

    @Test
    void saveReservationItemAlreadyReserved()
            throws ItemAlreadyReservedException, ItemNotAvailableException, TooManyReservationsException {
        postgresReservationRepository.saveReservation(newReservation());
        assertThrows(ItemAlreadyReservedException.class,
                () -> postgresReservationRepository.saveReservation(newReservation()));
    }

    @Test
    void getReservationsForUserOnlyNotDeleted()
            throws TooManyReservationsException, ItemNotAvailableException, ItemAlreadyReservedException {
        var reservation = newReservation();
        var deletedReservation = newReservation();
        deletedReservation.setDeletedAt(LocalDateTime.now().minusSeconds(1));
        postgresReservationRepository.saveReservation(deletedReservation);
        postgresReservationRepository.deleteReservation(deletedReservation);
        postgresReservationRepository.saveReservation(reservation);
        var reservations = postgresReservationRepository.getReservationsForUser(user);
        assertEquals(1, reservations.size());
    }

    @Test
    void deleteReservation()
            throws TooManyReservationsException, ItemNotAvailableException, ItemAlreadyReservedException {
        var reservation = new Reservation(UUID.randomUUID(), user.getId(), item,
                LocalDate.now().plusDays(1), null);
        postgresReservationRepository.saveReservation(reservation);
        postgresReservationRepository.deleteReservation(reservation);
        var reservations = postgresReservationRepository.getReservationsForUser(user);
        assertEquals(0, reservations.size());
        assertNotNull(reservation.getDeletedAt());
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

    private static void saveItem(Item item) {
        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection().prepareStatement(
                        """
                                insert into items (id, title, author, description, language, genre, isbn, item_type,
                                    pages, total, available, reserved) values (?, ?, ?, ?, ?, ?, ?, ?::item_type, ?, ?, ?, ?);
                                        """
                )
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
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateItemCounts(Item item) {
        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection().prepareStatement(
                        """
                                update items set available = ?, reserved = ? where id = ?;
                                        """
                )
        ) {
            statement.setInt(1, item.getAvailable());
            statement.setInt(2, item.getReserved());
            statement.setObject(3, item.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Reservation newReservation() {
        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID());
        reservation.setUserId(user.getId());
        reservation.setItem(item);
        reservation.setUntil(LocalDate.now().plusDays(1));
        return reservation;
    }

    private Reservation newReservationWithNewItem() {
        var newItem = new Item(UUID.randomUUID(), "title", "author", "description", "language", "genre", "isbn",
                ItemType.BOOK, 100, 10, 10, 0, null);
        saveItem(newItem);
        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID());
        reservation.setUserId(user.getId());
        reservation.setItem(newItem);
        reservation.setUntil(LocalDate.now().plusDays(1));
        return reservation;
    }

    private static void clearDatabase() {
        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection()
                        .prepareStatement("""
                                truncate table reservations;
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