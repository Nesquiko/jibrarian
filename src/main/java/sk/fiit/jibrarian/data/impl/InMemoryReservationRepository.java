package sk.fiit.jibrarian.data.impl;

import sk.fiit.jibrarian.data.ReservationRepository;
import sk.fiit.jibrarian.model.Reservation;
import sk.fiit.jibrarian.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InMemoryReservationRepository implements ReservationRepository {
    private static final Logger LOGGER = Logger.getLogger(InMemoryReservationRepository.class.getName());
    private final ConcurrentHashMap<UUID, List<Reservation>> reservations = new ConcurrentHashMap<>();

    @Override
    public void saveReservation(Reservation reservation) throws TooManyReservationsException {
        if (reservations.containsKey(reservation.getUserId())
                && (reservations.get(reservation.getUserId()).size() >= 3)) {
            LOGGER.log(Level.WARNING, "User with id {0} has too many reservations", reservation.getUserId());
            throw new TooManyReservationsException("User has too many reservations");
        }
        reservations.putIfAbsent(reservation.getUserId(), new ArrayList<>());
        reservations.get(reservation.getUserId()).add(reservation);
    }

    @Override
    public List<Reservation> getReservationsForUser(User user) {
        return reservations.getOrDefault(user.getId(), Collections.emptyList());
    }

    @Override
    public void deleteReservation(Reservation reservation) {
        if (reservations.containsKey(reservation.getUserId()))
            reservations.get(reservation.getUserId()).remove(reservation);
    }
}
