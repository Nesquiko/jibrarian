package sk.fiit.jibrarian.model;

import java.time.LocalDate;
import java.util.UUID;

public class Reservation {
    private UUID id;
    private UUID userId;
    private UUID itemId;
    private LocalDate until;

    public Reservation() {
    }

    public Reservation(UUID id, UUID userId, UUID itemId, LocalDate until) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
        this.until = until;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public LocalDate getUntil() {
        return until;
    }

    public void setUntil(LocalDate until) {
        this.until = until;
    }
}
