package model.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import model.entity.Item;
import model.entity.Member;
import model.persistence.DataStore;

/**
 * Service for managing item operations with business logic.
 */
public class ItemService {
  private final DataStore dataStore;
  private final Map<String, Item> items = new HashMap<>();
  private final TimeService time;
  private final MemberService memberService;

  /**
   * Constructor for ItemService.
   *
   * @param dataStore the persistence abstraction
   * @param time the global clock for creation dates
   * @param memberService the member service for owner validation
   */
  public ItemService(DataStore dataStore, TimeService time, MemberService memberService) {
    this.dataStore = Objects.requireNonNull(dataStore);
    this.time = Objects.requireNonNull(time);
    this.memberService = Objects.requireNonNull(memberService);

    // preload existing items
    for (Item it : dataStore.loadItems()) {
      items.put(it.getItemId(), it);
    }
  }

  /**
   * Creates, stores, and persists a new Item.
   *
   * @param name the item name
   * @param category the item category
   * @param description the item description
   * @param costPerDay the cost per day
   * @param ownerId the owner's ID
   * @return the created item
   */
  public Item createItem(String name,
                        Item.Category category,
                        String description,
                        double costPerDay,
                        String ownerId) {
    Member owner = memberService.findMemberById(ownerId);
    if (owner == null) {
      throw new IllegalArgumentException("No member with " + ownerId);
    }
    Item newItem = new Item(name, category, description, costPerDay, owner, time);
    
    owner.addItem(newItem);
    items.put(newItem.getItemId(), newItem);
    
    dataStore.saveItems(new ArrayList<>(items.values()));
    return newItem;
  }

  /**
   * Finds an item by ID.
   *
   * @param itemId the item ID
   * @return the item or null if not found
   */
  public Item findItemById(String itemId) {
    return items.get(itemId);
  }

  /**
   * Deletes an item.
   *
   * @param itemId the item ID to delete
   * @return true if deletion was successful
   */
  public boolean deleteItem(String itemId) {
    if (items.remove(itemId) != null) {
      dataStore.saveItems(new ArrayList<>(items.values()));
      return true;
    }
    return false;
  }

  /**
   * Updates an item.
   *
   * @param itemId the item ID
   * @param name the new name
   * @param category the new category
   * @param description the new description
   * @param costPerDay the new cost per day
   * @return true if update was successful
   */
  public boolean updateItem(String itemId, String name, Item.Category category, 
                           String description, double costPerDay) {
    Item item = items.get(itemId);
    if (item != null) {
      item.setName(name);
      item.setCategory(category);
      item.setDescription(description);
      item.setCostPerDay(costPerDay);
      dataStore.saveItems(new ArrayList<>(items.values()));
      return true;
    }
    return false;
  }

  /**
   * Lists all items.
   *
   * @return an unmodifiable list of all items
   */
  public List<Item> listAllItems() {
    return Collections.unmodifiableList(new ArrayList<>(items.values()));
  }
}
