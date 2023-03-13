package sk.fiit.jibrarian.model;

import java.time.LocalDate;
import java.util.UUID;

public class BorrowedItem {

    private UUID id;
    private UUID userId;
    private UUID itemId;
    private String title;
    private String author;
    private LocalDate until;
    private ItemStatus status;

    public BorrowedItem() {
    }

    public BorrowedItem(UUID id, UUID userId, UUID itemId, String title, String author, LocalDate until,
                        ItemStatus status) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
        this.title = title;
        this.author = author;
        this.until = until;
        this.status = status;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDate getUntil() {
        return until;
    }

    public void setUntil(LocalDate until) {
        this.until = until;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }
}
