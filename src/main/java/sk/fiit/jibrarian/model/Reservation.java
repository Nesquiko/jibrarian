package sk.fiit.jibrarian.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Reservation {
    private UUID id;
    private UUID userId;
    private UUID itemId;
    private LocalDate until;
    private LocalDateTime deletedAt;

    public Reservation() {
    }

    public Reservation(UUID id, UUID userId, UUID itemId, LocalDate until, LocalDateTime deletedAt) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
        this.until = until;
        this.deletedAt = deletedAt;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return id.equals(that.id) && userId.equals(that.userId) && itemId.equals(that.itemId) && until.equals(
                that.until) && Objects.equals(deletedAt, that.deletedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, itemId, until, deletedAt);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", userId=" + userId +
                ", itemId=" + itemId +
                ", until=" + until +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
