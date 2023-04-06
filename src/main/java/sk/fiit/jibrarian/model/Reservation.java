package sk.fiit.jibrarian.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Reservation {
    private UUID id;
    private UUID userId;
    private Item item;
    private LocalDate until;
    private LocalDateTime deletedAt;

    public Reservation() {
    }

    public Reservation(UUID id, UUID userId, Item item, LocalDate until, LocalDateTime deletedAt) {
        this.id = id;
        this.userId = userId;
        this.item = item;
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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
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
        return id.equals(that.id) && userId.equals(that.userId) && item.equals(that.item) && until.equals(
                that.until) && Objects.equals(deletedAt, that.deletedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, item, until, deletedAt);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", userId=" + userId +
                ", item=" + item +
                ", until=" + until +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
