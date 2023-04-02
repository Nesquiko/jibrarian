package sk.fiit.jibrarian.data;

import sk.fiit.jibrarian.data.CatalogRepository.ItemNotAvailableException;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.Reservation;
import sk.fiit.jibrarian.model.User;

import java.util.List;

public interface ReservationRepository {

    /**
     * Persists reservation. If user has more than 3 reservations, throws TooManyReservationsException.
     * If item is not available, throws ItemNotAvailableException.
     * Returns the item with updated count of available items.
     */
    Item saveReservation(Reservation reservation) throws TooManyReservationsException, ItemNotAvailableException;

    /**
     * Returns all reservations for user
     */
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
