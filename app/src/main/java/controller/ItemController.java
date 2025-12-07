package controller;

import java.util.List;
import model.entity.Contract;
import model.entity.Item;
import model.entity.Item.Category;
import model.service.ItemService;
import view.MenuView;

/**
 * Controller class for managing items.
 */
public class ItemController {
  private final ItemService itemService;

  /**
   * Constructor for ItemController.
   *
   * @param itemService the service to handle item business logic
   */
  public ItemController(ItemService itemService) {
    this.itemService = itemService;
  }

  /**
   * Handle create item from UI.
   *
   * @param view the view to interact with
   */
  public void handleCreateItem(MenuView view) {
    try {
      String name = view.prompt("Enter item name:");
      Category category = view.promptCategory("Enter category (TOOL, VEHICLE, GAME, TOY, SPORT, OTHER):");
      String description = view.prompt("Enter description:");
      double costPerDay = view.promptDouble("Enter cost per day:");
      String ownerId = view.prompt("Enter owner member ID:");
      
      Item item = createItem(name, category, description, costPerDay, ownerId);
      view.displaySuccess("Item created successfully! ID: " + item.getItemId());
    } catch (Exception e) {
      view.displayError("Failed to create item: " + e.getMessage());
    }
  }

  /**
   * Handle list items from UI.
   *
   * @param view the view to display results
   */
  public void handleListItems(MenuView view) {
    try {
      List<Item> items = getAllItems();
      view.displayItems(items);
    } catch (Exception e) {
      view.displayError("Failed to list items: " + e.getMessage());
    }
  }

  /**
   * Handle view item details from UI.
   *
   * @param view the view to interact with
   */
  public void handleViewItemDetails(MenuView view) {
    try {
      String itemId = view.prompt("Enter item ID:");
      Item item = itemService.findItemById(itemId);
      if (item == null) {
        view.displayError("Item not found with ID: " + itemId);
        return;
      }
      
      // Get contracts from the item itself
      List<Contract> contracts = item.getContracts();
      
      view.displayItemWithContracts(item, contracts);
    } catch (Exception e) {
      view.displayError("Failed to view item details: " + e.getMessage());
    }
  }

  /**
   * Handle edit item from UI.
   *
   * @param view the view to interact with
   */
  public void handleEditItem(MenuView view) {
    try {
      String itemId = view.prompt("Enter item ID to edit:");
      Item item = itemService.findItemById(itemId);
      if (item == null) {
        view.displayError("Item not found with ID: " + itemId);
        return;
      }
      
      view.displayItemDetails(item);
      System.out.println("\nEnter new values (press Enter to keep current value):");
      
      String name = view.prompt("Name [" + item.getName() + "]:");
      if (name.isEmpty()) {
        name = item.getName();
      }
      
      Category category = item.getCategory();
      String categoryStr = view.prompt("Category [" + category + "] (TOOL, VEHICLE, GAME, TOY, SPORT, OTHER):");
      if (!categoryStr.isEmpty()) {
        category = Category.valueOf(categoryStr.toUpperCase());
      }
      
      String description = view.prompt("Description [" + item.getDescription() + "]:");
      if (description.isEmpty()) {
        description = item.getDescription();
      }
      
      String costStr = view.prompt("Cost per day [" + item.getCostPerDay() + "]:");
      double costPerDay = costStr.isEmpty() ? item.getCostPerDay() : Double.parseDouble(costStr);
      
      boolean updated = updateItem(itemId, name, category, description, costPerDay);
      if (updated) {
        view.displaySuccess("Item updated successfully!");
        Item updatedItem = itemService.findItemById(itemId);
        view.displayItemDetails(updatedItem);
      } else {
        view.displayError("Failed to update item.");
      }
    } catch (Exception e) {
      view.displayError("Failed to edit item: " + e.getMessage());
    }
  }

  /**
   * Handle delete item from UI.
   *
   * @param view the view to interact with
   */
  public void handleDeleteItem(MenuView view) {
    try {
      String itemId = view.prompt("Enter item ID to delete:");
      Item item = itemService.findItemById(itemId);
      if (item == null) {
        view.displayError("Item not found with ID: " + itemId);
        return;
      }
      
      view.displayItemDetails(item);
      String confirm = view.prompt("Are you sure you want to delete this item? (yes/no):");
      
      if ("yes".equalsIgnoreCase(confirm.trim())) {
        boolean deleted = deleteItem(itemId);
        if (deleted) {
          view.displaySuccess("Item deleted successfully!");
        } else {
          view.displayError("Failed to delete item. It may have active contracts.");
        }
      } else {
        view.displaySuccess("Delete operation cancelled.");
      }
    } catch (Exception e) {
      view.displayError("Failed to delete item: " + e.getMessage());
    }
  }

  /**
   * Creates a new item.
   *
   * @param name the item name
   * @param category the item category
   * @param description the item description
   * @param costPerDay the cost per day
   * @param ownerId the owner's ID
   * @return the created item
   */
  public Item createItem(String name,
                        Category category,
                        String description,
                        double costPerDay,
                        String ownerId) {
    return itemService.createItem(name, category, description, costPerDay, ownerId);
  }

  /**
   * Gets all items.
   *
   * @return list of all items
   */
  public List<Item> getAllItems() {
    return itemService.listAllItems();
  }

  /**
   * Updates an item.
   *
   * @param itemId the item ID
   * @param newName the new name
   * @param newCategory the new category
   * @param newDescription the new description
   * @param newCostPerDay the new cost per day
   * @return true if update was successful
   */
  public boolean updateItem(String itemId, String newName, Category newCategory, 
                           String newDescription, 
                           double newCostPerDay) {
    return itemService.updateItem(itemId, newName, newCategory, newDescription, newCostPerDay);
  }

  /**
   * Deletes an item.
   *
   * @param itemId the item ID to delete
   * @return true if deletion was successful
   */
  public boolean deleteItem(String itemId) {
    return itemService.deleteItem(itemId);
  }
}
