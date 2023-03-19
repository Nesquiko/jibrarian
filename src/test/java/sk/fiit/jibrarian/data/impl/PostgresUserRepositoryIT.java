package sk.fiit.jibrarian.data.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.fiit.jibrarian.data.ConnectionPool;
import sk.fiit.jibrarian.data.ConnectionPool.ConnectionPoolBuilder;
import sk.fiit.jibrarian.data.UserRepository.AlreadyExistingUserException;
import sk.fiit.jibrarian.data.UserRepository.UserNotFound;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostgresUserRepositoryIT {
    private static ConnectionPool connectionPool;
    private PostgresUserRepository postgresUserRepository;
    private User user;

    @BeforeAll
    static void setUpClass() throws SQLException {
        connectionPool = new ConnectionPoolBuilder()
                .setHost("localhost")
                .setPort(42069)
                .setDatabase("jibrarian")
                .setUser("jibrarian")
                .setPassword("password")
                .build();
        clearDatabase();
    }

    @BeforeEach
    void setUp() {
        postgresUserRepository = new PostgresUserRepository(connectionPool);
        user = new User(UUID.randomUUID(), "email", "passHash", Role.MEMBER);
    }

    @Test
    void saveUserAlreadyExists() throws AlreadyExistingUserException {
        user.setEmail("saveUserAlreadyExists-test");
        postgresUserRepository.saveUser(user);
        assertThrows(AlreadyExistingUserException.class, () -> postgresUserRepository.saveUser(user));
    }

    @Test
    void saveUser() throws AlreadyExistingUserException {
        user.setEmail("saveUser-test");
        postgresUserRepository.saveUser(user);
        assertNotNull(postgresUserRepository.getUserByEmail(user.getEmail()));
    }

    @Test
    void getUserByEmail() throws AlreadyExistingUserException {
        user.setEmail("getUserByEmail-test");
        postgresUserRepository.saveUser(user);
        var userByEmail = postgresUserRepository.getUserByEmail(user.getEmail());
        assertTrue(userByEmail.isPresent());
        assertEquals(user, userByEmail.get());
    }

    @Test
    void getUserByEmailNotExists() {
        var userByEmail = postgresUserRepository.getUserByEmail("getUserByEmailNotExists-test");
        assertTrue(userByEmail.isEmpty());
    }

    @Test
    void updateUserNotExists() {
        assertThrows(UserNotFound.class, () -> postgresUserRepository.updateUser(user));
    }

    @Test
    void updateUser() throws AlreadyExistingUserException, UserNotFound {
        user.setEmail("updateUser-test");
        postgresUserRepository.saveUser(user);
        user.setRole(Role.ADMIN);
        postgresUserRepository.updateUser(user);
        var userByEmail = postgresUserRepository.getUserByEmail(user.getEmail());
        assertTrue(userByEmail.isPresent());
        assertEquals(user, userByEmail.get());
    }

    @Test
    void getAllLibrarians() throws AlreadyExistingUserException {
        user.setRole(Role.LIBRARIAN);
        user.setEmail("getAllLibrarians-test");
        postgresUserRepository.saveUser(user);
        var librarians = postgresUserRepository.getAllLibrarians();
        assertEquals(1, librarians.size());
        assertEquals(user, librarians.get(0));
    }

    @Test
    void getAllAdmins() throws AlreadyExistingUserException {
        user.setRole(Role.ADMIN);
        user.setEmail("getAllAdmins-test");
        postgresUserRepository.saveUser(user);
        var admins = postgresUserRepository.getAllAdmins();
        assertEquals(1, admins.size());
        assertEquals(user, admins.get(0));
    }

    private static void clearDatabase() {
        try (
                var connectionWrapper = connectionPool.getConnection();
                var statement = connectionWrapper.getConnection().prepareStatement("delete from users")
        ) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}