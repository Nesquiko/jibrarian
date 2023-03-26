package sk.fiit.jibrarian.data.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.fiit.jibrarian.data.ConnectionPool;
import sk.fiit.jibrarian.data.ConnectionPool.ConnectionPoolBuilder;
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
    void saveReservation() throws TooManyReservationsException {
        var reservation = new Reservation(UUID.randomUUID(), user.getId(), item,
                LocalDate.now().plusDays(1), null);
        postgresReservationRepository.saveReservation(reservation);
        var reservations = postgresReservationRepository.getReservationsForUser(user);
        assertEquals(1, reservations.size());
        assertEquals(reservation, reservations.get(0));
    }

    @Test
    void saveReservationTooManyReservations() throws TooManyReservationsException {
        postgresReservationRepository.saveReservation(
                new Reservation(UUID.randomUUID(), user.getId(), item, LocalDate.now().plusDays(1), null));
        postgresReservationRepository.saveReservation(
                new Reservation(UUID.randomUUID(), user.getId(), item, LocalDate.now().plusDays(1), null));
        postgresReservationRepository.saveReservation(
                new Reservation(UUID.randomUUID(), user.getId(), item, LocalDate.now().plusDays(1), null));
        assertThrows(TooManyReservationsException.class, () -> postgresReservationRepository.saveReservation(
                new Reservation(UUID.randomUUID(), user.getId(), item, LocalDate.now().plusDays(1), null)));
    }

    @Test
    void getReservationsForUserOnlyNotDeleted() throws TooManyReservationsException {
        var reservation = new Reservation(UUID.randomUUID(), user.getId(), item,
                LocalDate.now().plusDays(1), null);
        var deletedReservation = new Reservation(UUID.randomUUID(), user.getId(), item,
                LocalDate.now().plusDays(1), LocalDateTime.now().minusSeconds(1));
        postgresReservationRepository.saveReservation(reservation);
        postgresReservationRepository.saveReservation(deletedReservation);
        var reservations = postgresReservationRepository.getReservationsForUser(user);
        assertEquals(1, reservations.size());
        assertEquals(reservation, reservations.get(0));
    }

    @Test
    void deleteReservation() throws TooManyReservationsException {
        var reservation = new Reservation(UUID.randomUUID(), user.getId(), item,
                LocalDate.now().plusDays(1), null);
        postgresReservationRepository.saveReservation(reservation);
        postgresReservationRepository.deleteReservation(reservation);
        var reservations = postgresReservationRepository.getReservationsForUser(user);
        assertEquals(0, reservations.size());
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