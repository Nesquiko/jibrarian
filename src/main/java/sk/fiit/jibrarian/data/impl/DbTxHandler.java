package sk.fiit.jibrarian.data.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbTxHandler {
    protected void executeUpdateOrRollback(Connection connection, PreparedStatement statement) throws SQLException {
        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    protected ResultSet executeOrRollback(Connection connection, PreparedStatement statement) throws SQLException {
        try {
            return statement.executeQuery();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }
}
