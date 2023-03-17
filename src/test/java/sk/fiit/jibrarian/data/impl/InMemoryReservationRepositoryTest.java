package sk.fiit.jibrarian.data.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.fiit.jibrarian.data.ReservationRepository.TooManyReservationsException;
import sk.fiit.jibrarian.model.Reservation;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryReservationRepositoryTest {

    private InMemoryReservationRepository inMemoryReservationRepository;
    private User user;

    @BeforeEach
    void setUp() {
        inMemoryReservationRepository = new InMemoryReservationRepository();
        user = new User(UUID.randomUUID(), "email", "passHash", Role.MEMBER);

    }

    @Test
    void saveReservationSuccessfully() throws TooManyReservationsException {
        Reservation reservation = new Reservation();
        reservation.setUserId(user.getId());
        inMemoryReservationRepository.saveReservation(reservation);
        assertEquals(1, inMemoryReservationRepository.getReservationsForUser(user).size());
    }

    @Test
    void saveReservationTooManyReservations() throws TooManyReservationsException {
        Reservation reservation = new Reservation();
        reservation.setUserId(user.getId());
        inMemoryReservationRepository.saveReservation(reservation);
        inMemoryReservationRepository.saveReservation(reservation);
        inMemoryReservationRepository.saveReservation(reservation);
        assertThrows(TooManyReservationsException.class,
                () -> inMemoryReservationRepository.saveReservation(reservation));
    }

    @Test
    void getReservationsForUserNoReservations() {
        assertEquals(0, inMemoryReservationRepository.getReservationsForUser(user).size());
    }

    @Test
    void deleteReservationDeleteUnknownReservation() {
        Reservation reservation = new Reservation();
        reservation.setUserId(user.getId());
        inMemoryReservationRepository.deleteReservation(reservation);
        assertEquals(0, inMemoryReservationRepository.getReservationsForUser(user).size());
    }

    @Test
    void deleteReservationSuccessfully() throws TooManyReservationsException {
        Reservation reservation = new Reservation();
        reservation.setUserId(user.getId());
        inMemoryReservationRepository.saveReservation(reservation);
        assertEquals(1, inMemoryReservationRepository.getReservationsForUser(user).size());
        inMemoryReservationRepository.deleteReservation(reservation);
        assertEquals(0, inMemoryReservationRepository.getReservationsForUser(user).size());
    }

}