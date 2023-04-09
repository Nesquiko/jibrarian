package sk.fiit.jibrarian.data;

import sk.fiit.jibrarian.model.BorrowedItem;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.User;

import java.time.LocalDate;
import java.util.List;

public interface  CatalogRepository {

    /**
     * Persists item, if item already exists, throws ItemAlreadyExistsException
     */
    void saveItem(Item item) throws ItemAlreadyExistsException;

    /**
     * Returns a page of items, sorted alphabetically by title.
     *
     * @param page     which page to return, starts at 0
     * @param pageSize size of the page
     * @return page of items
     */
    Page getItemPage(Integer page, Integer pageSize);

    record Page(Integer page, Integer pageSize, Integer total, List<Item> items) {
    }

    /**
     * Updates stored item
     */
    void updateItem(Item item) throws ItemNotFoundException;

    /**
     * Lends item to user until the specified date. If the item isn't available, throws ItemNotAvailableException
     */
    BorrowedItem lendItem(Item item, User user, LocalDate until) throws ItemNotAvailableException;

    /**
     * Returns all items borrowed by the user
     */
    List<BorrowedItem> getBorrowedItemsForUser(User user);

    /**
     * Removes item from users borrowed items and returns it to the catalog.
     * Returns the item with updated count of available items.
     * If the item isn't found, throws ItemNotFoundException
     */
    Item returnItem(BorrowedItem borrowedItem) throws ItemNotFoundException;

    class ItemAlreadyExistsException extends Exception {
        public ItemAlreadyExistsException(String message) {
            super(message);
        }
    }

    class ItemNotFoundException extends Exception {
        public ItemNotFoundException(String message) {
            super(message);
        }
    }

    class ItemNotAvailableException extends Exception {
        public ItemNotAvailableException(String message) {
            super(message);
        }
    }
}
