package sk.fiit.jibrarian.data.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.fiit.jibrarian.data.CatalogRepository.ItemNotAvailableException;
import sk.fiit.jibrarian.data.ReservationRepository.ItemAlreadyReservedException;
import sk.fiit.jibrarian.data.ReservationRepository.TooManyReservationsException;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.ItemType;
import sk.fiit.jibrarian.model.Reservation;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryReservationRepositoryTest {

    private InMemoryReservationRepository inMemoryReservationRepository;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        inMemoryReservationRepository = new InMemoryReservationRepository();
        user = new User(UUID.randomUUID(), "email", "passHash", Role.MEMBER);
        item = new Item(UUID.randomUUID(), "title", "author", "description", "language", "genre", "isbn", ItemType.BOOK,
                100, 10, 10, 0, null);

    }

    @Test
    void saveReservationSuccessfully() throws TooManyReservationsException, ItemNotAvailableException,
            ItemAlreadyReservedException {
        Reservation reservation = newReservation();
        reservation.setUserId(user.getId());
        var savedItem = inMemoryReservationRepository.saveReservation(reservation);
        assertEquals(1, inMemoryReservationRepository.getReservationsForUser(user).size());
        assertEquals(item.getId(), savedItem.getId());
        assertEquals(item.getAvailable() - 1, savedItem.getAvailable());
        assertEquals(item.getReserved() + 1, savedItem.getReserved());
    }

    @Test
    void saveReservationTooManyReservations()
            throws TooManyReservationsException, ItemNotAvailableException, ItemAlreadyReservedException {
        Reservation reservation = newReservation();
        reservation.setUserId(user.getId());
        inMemoryReservationRepository.saveReservation(reservation);
        reservation = newReservationWithNewItem();
        inMemoryReservationRepository.saveReservation(reservation);
        reservation = newReservationWithNewItem();
        inMemoryReservationRepository.saveReservation(reservation);

        Reservation finalReservation = reservation;
        assertThrows(TooManyReservationsException.class,
                () -> inMemoryReservationRepository.saveReservation(finalReservation));
    }

    @Test
    void saveReservationItemNotAvailable() {
        Reservation reservation = newReservation();
        reservation.setUserId(user.getId());
        reservation.getItem().setAvailable(0);
        assertThrows(ItemNotAvailableException.class,
                () -> inMemoryReservationRepository.saveReservation(reservation));
    }

    @Test
    void saveReservationItemAlreadyReserved()
            throws ItemAlreadyReservedException, ItemNotAvailableException, TooManyReservationsException {
        Reservation reservation = newReservation();
        reservation.setUserId(user.getId());
        inMemoryReservationRepository.saveReservation(reservation);
        assertThrows(ItemAlreadyReservedException.class,
                () -> inMemoryReservationRepository.saveReservation(reservation));
    }

    @Test
    void getReservationsForUserNoReservations() {
        assertEquals(0, inMemoryReservationRepository.getReservationsForUser(user).size());
    }

    @Test
    void deleteReservationDeleteUnknownReservation() {
        Reservation reservation = newReservation();
        reservation.setUserId(user.getId());
        inMemoryReservationRepository.deleteReservation(reservation);
        assertEquals(0, inMemoryReservationRepository.getReservationsForUser(user).size());
    }

    @Test
    void deleteReservationSuccessfully()
            throws TooManyReservationsException, ItemNotAvailableException, ItemAlreadyReservedException {
        Reservation reservation = newReservation();
        reservation.setUserId(user.getId());
        inMemoryReservationRepository.saveReservation(reservation);
        assertEquals(1, inMemoryReservationRepository.getReservationsForUser(user).size());
        inMemoryReservationRepository.deleteReservation(reservation);
        assertEquals(0, inMemoryReservationRepository.getReservationsForUser(user).size());
    }

    private Reservation newReservation() {
        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID());
        reservation.setUserId(user.getId());
        reservation.setItem(item);
        reservation.setUntil(LocalDate.now().plusDays(1));
        return reservation;
    }

    private Reservation newReservationWithNewItem() {
        var newItem = new Item(UUID.randomUUID(), "title", "author", "description", "language", "genre", "isbn",
                ItemType.BOOK, 100, 10, 10, 0, null);
        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID());
        reservation.setUserId(user.getId());
        reservation.setItem(newItem);
        reservation.setUntil(LocalDate.now().plusDays(1));
        return reservation;
    }
}