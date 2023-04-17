package sk.fiit.jibrarian.model;

import java.util.Objects;
import java.util.UUID;

public class Item {

    private UUID id;
    private String title;
    private String author;
    private String description;
    private String language;
    private String genre;
    private String isbn;
    private ItemType itemType;
    private Integer pages;
    private Integer total;
    private Integer available;
    private Integer reserved;

    private Integer borrowed;

    private byte[] image;

    public Item() {
    }

    public Item(UUID id, String title, String author, String description, String language, String genre, String isbn,
                ItemType itemType, Integer pages, Integer total, Integer available, Integer reserved, Integer borrowed,
                byte[] image) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.language = language;
        this.genre = genre;
        this.isbn = isbn;
        this.itemType = itemType;
        this.pages = pages;
        this.total = total;
        this.available = available;
        this.reserved = reserved;
        this.borrowed = borrowed;
        this.image = image;
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

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Integer getBorrowed() {
        return borrowed;
    }

    public void setBorrowed(Integer borrowed) {
        this.borrowed = borrowed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id.equals(item.id) && title.equals(item.title) && author.equals(item.author) && description.equals(
                item.description) && language.equals(item.language) && genre.equals(item.genre) && isbn.equals(
                item.isbn) && itemType == item.itemType && pages.equals(item.pages) && total.equals(
                item.total) && available.equals(item.available) && reserved.equals(item.reserved) && borrowed.equals(
                item.borrowed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, description, language, genre, isbn, itemType, pages, total, available,
                reserved, borrowed);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", genre='" + genre + '\'' +
                ", isbn='" + isbn + '\'' +
                ", itemType=" + itemType +
                ", pages=" + pages +
                ", total=" + total +
                ", available=" + available +
                ", reserved=" + reserved +
                ", borrowed=" + borrowed +
                '}';
    }
}
