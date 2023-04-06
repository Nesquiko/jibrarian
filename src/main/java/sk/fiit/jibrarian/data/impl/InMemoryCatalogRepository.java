package sk.fiit.jibrarian.data.impl;

import sk.fiit.jibrarian.data.CatalogRepository;
import sk.fiit.jibrarian.model.BorrowedItem;
import sk.fiit.jibrarian.model.Item;
import sk.fiit.jibrarian.model.User;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InMemoryCatalogRepository implements CatalogRepository {
    private static final Logger LOGGER = Logger.getLogger(InMemoryCatalogRepository.class.getName());
    private final ConcurrentHashMap<UUID, Item> items = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, BorrowedItem> borrowedItems = new ConcurrentHashMap<>();

    @Override
    public void saveItem(Item item) throws ItemAlreadyExistsException {
        if (items.containsKey(item.getId())) {
            LOGGER.log(Level.WARNING, "Item with id {0} already exists", item.getId());
            throw new ItemAlreadyExistsException(String.format("Item with id %s already exists", item.getId()));
        }
        items.put(item.getId(), item);
    }

    @Override
    public Page getItemPage(Integer page, Integer pageSize) {
        var start = page * pageSize;
        var end = start + pageSize;
        var itemPage = items.values()
                .stream()
                .sorted(Comparator.comparing(Item::getTitle))
                .skip(start)
                .limit(end)
                .toList();
        return new Page(page, pageSize, items.size(), itemPage);
    }

    @Override
    public void updateItem(Item item) throws ItemNotFoundException {
        if (!items.containsKey(item.getId())) {
            LOGGER.log(Level.WARNING, "Item with id {0} not found", item.getId());
            throw new ItemNotFoundException(String.format("Item with id %s not found", item.getId()));
        }
        items.put(item.getId(), item);
    }

    @Override
    public BorrowedItem lendItem(Item item, User user, LocalDate until) throws ItemNotAvailableException {
        var itemToBorrow = items.get(item.getId());
        if (itemToBorrow.getAvailable() - 1 < 0) {
            LOGGER.log(Level.WARNING, "Item with id {0} not available", item.getId());
            throw new ItemNotAvailableException(String.format("Item with id %s not available", item.getId()));
        }
        itemToBorrow.setAvailable(itemToBorrow.getAvailable() - 1);
        var borrowedItem = createNewBorrowedItem(itemToBorrow, user, until);
        borrowedItems.put(borrowedItem.getId(), borrowedItem);
        return borrowedItem;
    }

    @Override
    public List<BorrowedItem> getBorrowedItemsForUser(User user) {
        return borrowedItems.values()
                .stream()
                .filter(borrowedItem -> borrowedItem.getUserId().equals(user.getId()))
                .toList();
    }

    @Override
    public Item returnItem(BorrowedItem borrowedItem) throws ItemNotFoundException {
        var item = items.get(borrowedItem.getItem().getId());
        item.setAvailable(item.getAvailable() + 1);
        updateItem(item);
        borrowedItems.remove(borrowedItem.getId());
        return item;
    }

    private BorrowedItem createNewBorrowedItem(Item item, User user, LocalDate until) {
        return new BorrowedItem(UUID.randomUUID(), user.getId(), item, until);
    }
}
