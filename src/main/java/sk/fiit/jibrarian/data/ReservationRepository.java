package sk.fiit.jibrarian.data;

import sk.fiit.jibrarian.model.Reservation;
import sk.fiit.jibrarian.model.User;

import java.util.List;

public interface ReservationRepository {

    /**
     * Persists reservation. If user has more than 3 reservations, throws
     */
    void saveReservation(Reservation reservation) throws TooManyReservationsException;

    List<Reservation> getReservationsForUser(User user);

    /**
     * Deletes reservation
     */
    void deleteReservation(Reservation reservation);

    class TooManyReservationsException extends Exception {
        public TooManyReservationsException(String message) {
            super(message);
        }
    }
}
