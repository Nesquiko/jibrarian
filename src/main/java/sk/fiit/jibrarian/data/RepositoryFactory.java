package sk.fiit.jibrarian.data;

import sk.fiit.jibrarian.data.ConnectionPool.ConnectionPoolBuilder;
import sk.fiit.jibrarian.data.impl.InMemoryCatalogRepository;
import sk.fiit.jibrarian.data.impl.InMemoryReservationRepository;
import sk.fiit.jibrarian.data.impl.InMemoryUserRepository;
import sk.fiit.jibrarian.data.impl.PostgresCatalogRepository;
import sk.fiit.jibrarian.data.impl.PostgresReservationRepository;
import sk.fiit.jibrarian.data.impl.PostgresUserRepository;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RepositoryFactory {
    private static final Logger LOGGER = Logger.getLogger(RepositoryFactory.class.getName());
    private static final String ENVIRONMENT_PROPERTY = "db";
    private static final String IN_MEMORY_ENVIRONMENT = "in-memory";
    private static final String DB_ENVIRONMENT = "db";
    private static final String DB_HOST_PROP = "db.host";
    private static final String DB_PORT_PROP = "db.port";
    private static final String DB_NAME_PROP = "db.name";
    private static final String DB_USER_PROP = "db.user";
    private static final String DB_PASSWORD_PROP = "db.password";

    private static CatalogRepository catalogRepository;
    private static UserRepository userRepository;
    private static ReservationRepository reservationRepository;

    public static class EnvironmentSetupException extends Exception {
        public EnvironmentSetupException(String message) {
            super(message);
        }
    }

    public static class UninitializedRepositoryException extends RuntimeException {
        public UninitializedRepositoryException(String message) {
            super(message);
        }
    }

    private RepositoryFactory() {
    }

    public static void initializeEnvironment() throws EnvironmentSetupException {
        var env = System.getProperty(ENVIRONMENT_PROPERTY);
        if (Objects.isNull(env)) {
            LOGGER.log(Level.INFO, "No environment specified, using in-memory environment");
            env = "db";
        }

        switch (env) {
            case IN_MEMORY_ENVIRONMENT -> initializeInMemoryEnv();
            case DB_ENVIRONMENT -> initializeDbEnv();
            default -> throw new EnvironmentSetupException("Unknown environment: " + env);
        }
    }

    public static CatalogRepository getCatalogRepository() throws UninitializedRepositoryException {
        if (Objects.isNull(catalogRepository)) {
            LOGGER.log(Level.SEVERE, "Catalog repository is not initialized");
            throw new UninitializedRepositoryException("Catalog repository is not initialized");
        }

        return catalogRepository;
    }

    public static UserRepository getUserRepository() throws UninitializedRepositoryException {
        if (Objects.isNull(userRepository)) {
            LOGGER.log(Level.SEVERE, "User repository is not initialized");
            throw new UninitializedRepositoryException("User repository is not initialized");
        }

        return userRepository;
    }

    public static ReservationRepository getReservationRepository() throws UninitializedRepositoryException {
        if (Objects.isNull(reservationRepository)) {
            LOGGER.log(Level.SEVERE, "Reservation repository is not initialized");
            throw new UninitializedRepositoryException("Reservation repository is not initialized");
        }

        return reservationRepository;
    }

    private static void setCatalogRepository(CatalogRepository catalogRepository) {
        RepositoryFactory.catalogRepository = catalogRepository;
    }

    private static void setUserRepository(UserRepository userRepository) {
        RepositoryFactory.userRepository = userRepository;
    }

    private static void setReservationRepository(ReservationRepository reservationRepository) {
        RepositoryFactory.reservationRepository = reservationRepository;
    }

    private static void initializeInMemoryEnv() {
        LOGGER.log(Level.INFO, "Using in-memory environment");
        setCatalogRepository(new InMemoryCatalogRepository());
        setUserRepository(new InMemoryUserRepository());
        setReservationRepository(new InMemoryReservationRepository());
    }

    private static void initializeDbEnv() throws EnvironmentSetupException {
        LOGGER.log(Level.INFO, "Using db environment");
        try {
            var connectionPool = connectToDb();
            setCatalogRepository(new PostgresCatalogRepository(connectionPool));
            setUserRepository(new PostgresUserRepository(connectionPool));
            setReservationRepository(new PostgresReservationRepository(connectionPool));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to db", e);
            throw new EnvironmentSetupException("Failed to connect to db");
        }
    }


    private static ConnectionPool connectToDb() throws SQLException, EnvironmentSetupException {
        var host = "localhost";
        var port = "42069";
        var dbName = "jibrarian";
        var user = "jibrarian";
        var password = "password";

        if (Objects.isNull(host))
            throw new EnvironmentSetupException("No db host specified");
        if (Objects.isNull(port))
            throw new EnvironmentSetupException("No db port specified");
        if (Objects.isNull(dbName))
            throw new EnvironmentSetupException("No db name specified");
        if (Objects.isNull(user))
            throw new EnvironmentSetupException("No db user specified");
        if (Objects.isNull(password))
            throw new EnvironmentSetupException("No db password specified");

        int portInt;
        try {
            portInt = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            throw new EnvironmentSetupException("Invalid db port specified: " + port);
        }

        return new ConnectionPoolBuilder()
                .setHost(host)
                .setPort(portInt)
                .setDatabase(dbName)
                .setUser(user)
                .setPassword(password)
                .setMaxPoolSize(3)
                .setInitialPoolSize(0)
                .build();
    }
}
