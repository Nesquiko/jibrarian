package sk.fiit.jibrarian.data.impl;

import sk.fiit.jibrarian.data.ConnectionPool;
import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PostgresUserRepository implements UserRepository {

    private final ConnectionPool connectionPool;

    public PostgresUserRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void saveUser(User user) throws AlreadyExistingUserException {
        try (
                var connectionWrapper = connectionPool.getConnection();
                var statement = connectionWrapper.getConnection().prepareStatement(
                        "insert into users (id, email, pass_hash, role) values (?, ?, ?, ?::user_role)")
        ) {
            statement.setObject(1, user.getId());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassHash());
            statement.setString(4, user.getRole().getDbValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            // TODO log as error
            if (e.getSQLState().equals("23505")) {
                throw new AlreadyExistingUserException("User with email " + user.getEmail() + " already exists");
            }
        }
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        try (
                var connectionWrapper = connectionPool.getConnection();
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
            // TODO log as error
        }
        return Optional.empty();
    }

    @Override
    public void updateUser(User user) throws UserNotFound {
        try (
                var connectionWrapper = connectionPool.getConnection();
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
            // TODO log as error
        }
    }

    @Override
    public List<User> getAllLibrarians() {
        try (
                var connectionWrapper = connectionPool.getConnection();
                var statement = connectionWrapper.getConnection().prepareStatement(
                        "select id, email, pass_hash, role from users where role = 'librarian'")
        ) {
            return getUsersWithRole(statement);
        } catch (SQLException e) {
            // TODO log as error
        }
        return List.of();
    }

    @Override
    public List<User> getAllAdmins() {
        try (
                var connectionWrapper = connectionPool.getConnection();
                var statement = connectionWrapper.getConnection().prepareStatement(
                        "select id, email, pass_hash, role from users where role = 'admin'")
        ) {
            return getUsersWithRole(statement);
        } catch (SQLException e) {
            // TODO log as error
        }
        return List.of();
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
