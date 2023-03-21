package sk.fiit.jibrarian.data.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.fiit.jibrarian.data.CatalogRepository.ItemAlreadyExistsException;
import sk.fiit.jibrarian.data.CatalogRepository.ItemNotAvailableException;
import sk.fiit.jibrarian.data.CatalogRepository.ItemNotFoundException;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.ItemType;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryCatalogRepositoryTest {

    private InMemoryCatalogRepository inMemoryCatalogRepository;
    private Item item;
    private User user;

    @BeforeEach
    void setUp() {
        inMemoryCatalogRepository = new InMemoryCatalogRepository();
        item = createItem("title");
        user = new User(UUID.randomUUID(), "name", "email", Role.ADMIN);
    }

    @Test
    void saveItemSuccessfully() throws ItemAlreadyExistsException {
        inMemoryCatalogRepository.saveItem(item);
        var items = inMemoryCatalogRepository.getItemPage(0, 1);
        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
    }

    @Test
    void saveItemAlreadyExits() throws ItemAlreadyExistsException {
        inMemoryCatalogRepository.saveItem(item);
        assertThrows(ItemAlreadyExistsException.class, () -> inMemoryCatalogRepository.saveItem(item));
    }

    @Test
    void getItemPageCorrectPaging() throws ItemAlreadyExistsException {
        var item2 = createItem(item.getTitle() + "2");
        inMemoryCatalogRepository.saveItem(item);
        inMemoryCatalogRepository.saveItem(item2);

        var items = inMemoryCatalogRepository.getItemPage(0, 1);
        assertEquals(1, items.size());
        assertEquals(item, items.get(0));

        items = inMemoryCatalogRepository.getItemPage(1, 1);
        assertEquals(1, items.size());
        assertEquals(item2, items.get(0));
    }

    @Test
    void updateItemSuccessfully() throws ItemAlreadyExistsException, ItemNotFoundException {
        inMemoryCatalogRepository.saveItem(item);

        var updatedItem = createItem(item.getTitle() + "2");
        updatedItem.setId(item.getId());

        inMemoryCatalogRepository.updateItem(updatedItem);

        var items = inMemoryCatalogRepository.getItemPage(0, 1);
        assertEquals(1, items.size());
        assertEquals(updatedItem, items.get(0));
    }

    @Test
    void updateItemItemNotFound() {
        assertThrows(ItemNotFoundException.class, () -> inMemoryCatalogRepository.updateItem(item));
    }

    @Test
    void lendItemCorrectAvailable() throws ItemAlreadyExistsException, ItemNotAvailableException {
        inMemoryCatalogRepository.saveItem(item);
        inMemoryCatalogRepository.lendItem(item, user, LocalDate.now().plusDays(1));
        var items = inMemoryCatalogRepository.getItemPage(0, 1);
        assertEquals(0, items.get(0).getAvailable());
    }

    @Test
    void lendItemNotAvailable() throws ItemAlreadyExistsException, ItemNotAvailableException {
        inMemoryCatalogRepository.saveItem(item);
        inMemoryCatalogRepository.lendItem(item, user, LocalDate.now().plusDays(1));
        assertThrows(ItemNotAvailableException.class,
                () -> inMemoryCatalogRepository.lendItem(item, user, LocalDate.now().plusDays(1)));
    }

    @Test
    void getBorrowedItemsForUserSuccessfully() throws ItemAlreadyExistsException, ItemNotAvailableException {
        inMemoryCatalogRepository.saveItem(item);
        var borrowedItem = inMemoryCatalogRepository.lendItem(item, user, LocalDate.now().plusDays(1));

        var items = inMemoryCatalogRepository.getBorrowedItemsForUser(user);
        assertEquals(1, items.size());
        assertEquals(borrowedItem, items.get(0));
    }

    @Test
    void returnItemSuccessfully() throws ItemAlreadyExistsException, ItemNotAvailableException, ItemNotFoundException {
        inMemoryCatalogRepository.saveItem(item);
        var borrowedItem = inMemoryCatalogRepository.lendItem(item, user, LocalDate.now().plusDays(1));
        inMemoryCatalogRepository.returnItem(borrowedItem);
        var items = inMemoryCatalogRepository.getItemPage(0, 1);
        assertEquals(1, items.get(0).getAvailable());
    }

    private Item createItem(String title) {
        return new Item(UUID.randomUUID(), title, "author", "description", "language", "genre", "isbn", ItemType.BOOK,
                1, 1, 1, 1, null);
    }
}