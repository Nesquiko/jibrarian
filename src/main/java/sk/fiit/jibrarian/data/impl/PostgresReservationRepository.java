package sk.fiit.jibrarian.data.impl;

import sk.fiit.jibrarian.data.ConnectionPool;
import sk.fiit.jibrarian.data.ReservationRepository;
import sk.fiit.jibrarian.model.Reservation;
import sk.fiit.jibrarian.model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PostgresReservationRepository implements ReservationRepository {

    private final ConnectionPool connectionPool;

    public PostgresReservationRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void saveReservation(Reservation reservation) throws TooManyReservationsException {
        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection().prepareStatement(
                        "select count(*) from reservations where user_id = ? and deleted_at is null")
        ) {
            statement.setObject(1, reservation.getUserId());
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                if (resultSet.getInt(1) >= 3) {
                    throw new TooManyReservationsException(
                            String.format("User %s has too many reservations", reservation.getUserId()));
                }
            }
        } catch (SQLException e) {
            // TODO log as error
        }

        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection().prepareStatement(
                        "insert into reservations (id, user_id, item_id, until, deleted_at) values (?, ?, ?, ?, ?)")
        ) {
            statement.setObject(1, reservation.getId());
            statement.setObject(2, reservation.getUserId());
            statement.setObject(3, reservation.getItemId());
            statement.setObject(4, reservation.getUntil());
            statement.setObject(5, reservation.getDeletedAt());
            statement.executeUpdate();
        } catch (SQLException e) {
            // TODO log as error
        }
    }

    @Override
    public List<Reservation> getReservationsForUser(User user) {
        try (
                var connectionWrapper = connectionPool.getConnWrapper();
                var statement = connectionWrapper.getConnection().prepareStatement(
                        "select id, user_id, item_id, until, deleted_at from reservations where user_id = ? and deleted_at is null")
        ) {
            statement.setObject(1, user.getId());
            var resultSet = statement.executeQuery();
            var reservations = new ArrayList<Reservation>();
            while (resultSet.next()) {
                reservations.add(new Reservation(
                        resultSet.getObject("id", UUID.class),
                        resultSet.getObject("user_id", UUID.class),
                        resultSet.getObject("item_id", UUID.class),
                        resultSet.getObject("until", LocalDate.class),
                        resultSet.getObject("deleted_at", LocalDateTime.class)
                ));
            }
            return reservations;
        } catch (SQLException e) {
            // TODO log as error
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
            // TODO log as error
        }
    }
}
