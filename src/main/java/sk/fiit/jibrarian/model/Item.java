package sk.fiit.jibrarian.model;

import java.util.UUID;

public class Item {

    private UUID id;
    private String title;
    private String author;
    private String description;
    private String language;
    private String genre;
    private ItemType itemType;
    private Integer pages;
    private Integer total;
    private Integer available;
    private Integer reserved;

    public Item() {
    }

    public Item(UUID id, String title, String author, String description, String language, String genre,
                ItemType itemType,
                Integer pages, Integer total, Integer available, Integer reserved) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.language = language;
        this.genre = genre;
        this.itemType = itemType;
        this.pages = pages;
        this.total = total;
        this.available = available;
        this.reserved = reserved;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public Integer getReserved() {
        return reserved;
    }

    public void setReserved(Integer reserved) {
        this.reserved = reserved;
    }
}
