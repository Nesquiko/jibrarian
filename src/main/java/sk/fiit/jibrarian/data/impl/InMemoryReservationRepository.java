package sk.fiit.jibrarian.data.impl;

import sk.fiit.jibrarian.data.CatalogRepository.ItemNotAvailableException;
import sk.fiit.jibrarian.data.ReservationRepository;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.Reservation;
import sk.fiit.jibrarian.model.User;

import java.time.LocalDateTime;
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
    public Item saveReservation(Reservation reservation)
            throws TooManyReservationsException, ItemNotAvailableException, ItemAlreadyReservedException {
        if (reservations.containsKey(reservation.getUserId())
                && (reservations.get(reservation.getUserId()).size() >= 3)) {
            LOGGER.log(Level.WARNING, "User with id {0} has too many reservations", reservation.getUserId());
            throw new TooManyReservationsException("User has too many reservations");
        }

        if (reservations.containsKey(reservation.getUserId())
                && reservations.get(reservation.getUserId()).stream()
                .anyMatch(r -> r.getItem().getId().equals(reservation.getItem().getId()))) {
            LOGGER.log(Level.WARNING, "User with id {0} has already reserved item with id {1}",
                    new Object[]{reservation.getUserId(), reservation.getItem().getId()});
            throw new ItemAlreadyReservedException("User has already reserved this item");
        }

        var item = reservation.getItem();
        var newReserved = item.getReserved() + 1;
        var newAvailable = item.getAvailable() - 1;
        if (newAvailable < 0) {
            LOGGER.log(Level.WARNING, "Item with id {0} is not available", reservation.getId());
            throw new ItemNotAvailableException("Item is not available");
        }
        reservations.putIfAbsent(reservation.getUserId(), new ArrayList<>());
        reservations.get(reservation.getUserId()).add(reservation);

        return newUpdatedItem(item, newReserved, newAvailable);
    }

    @Override
    public List<Reservation> getReservationsForUser(User user) {
        return reservations.getOrDefault(user.getId(), Collections.emptyList())
                .stream().filter(r -> r.getDeletedAt() == null).toList();
    }

    @Override
    public void deleteReservation(Reservation reservation) {
        if (!reservations.containsKey(reservation.getUserId()))
            return;

        var usersReservations = reservations.get(reservation.getUserId());
        if (!usersReservations.contains(reservation))
            return;

        var toBeDeleted = usersReservations.stream()
                .filter(r -> r.getId().equals(reservation.getId()))
                .findFirst()
                .orElseThrow();

        var item = toBeDeleted.getItem();
        var newReserved = item.getReserved() - 1;
        var newAvailable = item.getAvailable() + 1;
        item.setReserved(newReserved);
        item.setAvailable(newAvailable);
        toBeDeleted.setDeletedAt(LocalDateTime.now());
    }

    private Item newUpdatedItem(Item item, int newReserved, int newAvailable) {
        return new Item(
                item.getId(),
                item.getTitle(),
                item.getAuthor(),
                item.getDescription(),
                item.getLanguage(),
                item.getGenre(),
                item.getIsbn(),
                item.getItemType(),
                item.getPages(),
                item.getTotal(),
                newAvailable,
                newReserved,
                item.getBorrowed(),
                item.getImage()
        );
    }
}
