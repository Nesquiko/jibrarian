package sk.fiit.jibrarian.data.impl;

import sk.fiit.jibrarian.data.ConnectionPool;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgresUserRepository implements UserRepository {
    private static final Logger LOGGER = Logger.getLogger(PostgresUserRepository.class.getName());
    private final ConnectionPool connectionPool;

    private User currentlyLoggedInUser;

    public PostgresUserRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void saveCurrentlyLoggedInUser(User user) {
        this.currentlyLoggedInUser = user;
    }

    @Override
    public Optional<User> getCurrentlyLoggedInUser() {
        return Optional.ofNullable(currentlyLoggedInUser);
    }

    @Override
    public void saveUser(User user) throws AlreadyExistingUserException {
        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection().prepareStatement(
                        "insert into users (id, email, pass_hash, role) values (?, ?, ?, ?::user_role)")
        ) {
            statement.setObject(1, user.getId());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassHash());
            statement.setString(4, user.getRole().getDbValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                LOGGER.log(Level.WARNING, "User with email {0} already exists", user.getEmail());
                throw new AlreadyExistingUserException("User with email " + user.getEmail() + " already exists");
            }
            LOGGER.log(Level.SEVERE, "Error while saving user", e);
        }
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection().prepareStatement(
                        "select id, email, pass_hash, role from users where email = ?")
        ) {
            statement.setString(1, email);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new User(
                        resultSet.getObject("id", UUID.class),
                        resultSet.getString("email"),
                        resultSet.getString("pass_hash"),
                        Role.fromDbValue(resultSet.getString("role"))
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while getting user by email", e);
        }
        return Optional.empty();
    }

    @Override
    public void updateUser(User user) throws UserNotFound {
        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection().prepareStatement(
                        "update users set email = ?, pass_hash = ?, role = ?::user_role where id = ?")
        ) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getPassHash());
            statement.setString(3, user.getRole().getDbValue());
            statement.setObject(4, user.getId());
            int updated = statement.executeUpdate();
            if (updated == 0)
                throw new UserNotFound("User with id " + user.getId() + " not found");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while updating user", e);
        }
    }

    @Override
    public void deleteUser(User user) throws UserNotFound {
        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection().prepareStatement(
                        "delete from users where id = ?")
        ) {
            statement.setObject(1, user.getId());
            int updated = statement.executeUpdate();
            if (updated == 0)
                throw new UserNotFound("User with id " + user.getId() + " not found");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while deleting user", e);
        }
    }

    @Override
    public List<User> getAllLibrarians() {
        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection().prepareStatement(
                        "select id, email, pass_hash, role from users where role = 'librarian'")
        ) {
            return getUsersWithRole(statement);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while getting all librarians", e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<User> getAllAdmins() {
        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection().prepareStatement(
                        "select id, email, pass_hash, role from users where role = 'admin'")
        ) {
            return getUsersWithRole(statement);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while getting all admins", e);
        }
        return Collections.emptyList();
    }

    private List<User> getUsersWithRole(PreparedStatement statement) throws SQLException {
        var resultSet = statement.executeQuery();
        var users = new ArrayList<User>();
        while (resultSet.next()) {
            users.add(new User(
                    resultSet.getObject("id", UUID.class),
                    resultSet.getString("email"),
                    resultSet.getString("pass_hash"),
                    Role.fromDbValue(resultSet.getString("role"))
            ));
        }
        return users;
    }
}
