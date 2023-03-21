package sk.fiit.jibrarian.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {
    private final String url;
    private final String user;
    private final String password;
    private final int maxPoolSize;
    private final int maxTimeout;
    private final List<Connection> connPool;
    private final List<Connection> usedConnections = new ArrayList<>();

    public static class ConnectionPoolBuilder {
        private static final int DEFUALT_INITIAL_POOL_SIZE = 1;
        private static final int DEFUALT_MAX_POOL_SIZE = 3;
        private static final int DEFAULT_MAX_TIMEOUT = 5;
        private int initialPoolSize = DEFUALT_INITIAL_POOL_SIZE;
        private int maxPoolSize = DEFUALT_MAX_POOL_SIZE;
        private int maxTimeout = DEFAULT_MAX_TIMEOUT;
        private String host;
        private Integer port;
        private String database;
        private String user;
        private String password;

        public ConnectionPoolBuilder setHost(String host) {
            this.host = host;
            return this;
        }

        public ConnectionPoolBuilder setPort(Integer port) {
            this.port = port;
            return this;
        }

        public ConnectionPoolBuilder setDatabase(String database) {
            this.database = database;
            return this;
        }

        public ConnectionPoolBuilder setUser(String user) {
            this.user = user;
            return this;
        }

        public ConnectionPoolBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public ConnectionPoolBuilder setInitialPoolSize(int initialPoolSize) {
            this.initialPoolSize = initialPoolSize;
            return this;
        }

        public ConnectionPoolBuilder setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
            return this;
        }

        public ConnectionPoolBuilder setMaxTimeout(int maxTimeout) {
            this.maxTimeout = maxTimeout;
            return this;
        }

        public ConnectionPool build() throws SQLException {
            String url = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
            return new ConnectionPool(url, user, password, initialPoolSize, maxPoolSize, maxTimeout);
        }
    }

    public static class ConnectionWrapper implements AutoCloseable {
        private final Connection connection;
        private final ConnectionPool connectionPool;

        public ConnectionWrapper(Connection connection, ConnectionPool connectionPool) {
            this.connection = connection;
            this.connectionPool = connectionPool;
        }

        public Connection getConnection() {
            return connection;
        }

        @Override
        public void close() {
            connectionPool.releaseConnection(connection);
        }
    }

    private ConnectionPool(String url, String user, String password, int initialPoolSize, int maxPoolSize,
                           int maxTimeout
    ) throws SQLException {
        this.url = url;
        this.user = user;
        this.password = password;
        this.connPool = new ArrayList<>(initialPoolSize);
        this.maxPoolSize = maxPoolSize;
        this.maxTimeout = maxTimeout;
        for (int i = 0; i < initialPoolSize; i++) {
            connPool.add(createConnection());
        }
    }

    public ConnectionWrapper getConnWrapper() throws SQLException {
        while (connPool.isEmpty()) {
            if (usedConnections.size() < maxPoolSize) {
                connPool.add(createConnection());
            }
        }

        Connection connection = connPool.remove(connPool.size() - 1);
        if (!connection.isValid(maxTimeout)) {
            connection.close();
            connection = createConnection();
        }

        usedConnections.add(connection);
        return new ConnectionWrapper(connection, this);
    }

    private void releaseConnection(Connection connection) {
        usedConnections.remove(connection);
        connPool.add(connection);
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void close() throws SQLException {
        for (Connection connection : usedConnections)
            releaseConnection(connection);
        for (Connection connection : connPool)
            connection.close();

        connPool.clear();
    }
}
