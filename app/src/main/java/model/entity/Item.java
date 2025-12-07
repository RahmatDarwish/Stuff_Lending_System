package model.entity;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import model.service.TimeService;

/**
 * Class Item representing an item in the lending system.
 */
public class Item {
  private static final String ALPHANUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final SecureRandom RAND = new SecureRandom();
  
  private final String itemId;
  private String name;
  private Category category;
  private String description;
  private double costPerDay;
  private final int creationDate;
  private final TimeService time;
  private final Member owner; // owner of the item
  private List<Contract> contracts; // collection of contracts for the item

  /**
   * Constructor for item.
   *
   * @param name of the item
   * @param category the item belongs to
   * @param description of the item
   * @param costPerDay for item
   * @param owner for owner of the item
   * @param time the time service
   */
  public Item(String name, Category category, String description, double costPerDay, 
              Member owner, TimeService time) {
    this.itemId = generateUniqueId();
    this.time = time;
    setName(name);
    setCategory(category);
    setDescription(description);
    setCostPerDay(costPerDay);
    this.creationDate = time.getCurrentDay();
    this.owner = Objects.requireNonNull(owner, "Owner required");
    this.contracts = new ArrayList<>();
  }

  /**
   * Copy constructor.
   *
   * @param item the item to copy
   */
  public Item(Item item) {
    this.itemId = item.itemId;
    this.name = item.name;
    this.category = item.category;
    this.description = item.description;
    this.costPerDay = item.costPerDay;
    this.creationDate = item.creationDate;
    this.time = item.time;
    this.owner = item.owner;
    this.contracts = new ArrayList<>(item.contracts);
  }

  /**
   * Gets the item ID.
   *
   * @return the item ID
   */
  public String getItemId() {  
    return itemId;
  }

  /**
   * Gets the item name.
   *
   * @return the item name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the item category.
   *
   * @return the item category
   */
  public Category getCategory() {
    return category;
  }

  /**
   * Gets the item description.
   *
   * @return the item description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the cost per day.
   *
   * @return the cost per day
   */
  public double getCostPerDay() {
    return costPerDay;
  }

  /**
   * Calculates cost per day (placeholder for strategy pattern).
   *
   * @param days the number of days
   * @return the cost per day
   */
  public double calcCostPerDay(int days) {
    if (days < 0) {
      throw new IllegalArgumentException("Days must be >= 0");
    }
    return costPerDay;
  }

  /**
   * Gets the creation date.
   *
   * @return the creation date
   */
  public int getCreationDate() {
    return creationDate;
  }

  /**
   * Gets the owner (returns copy to maintain encapsulation).
   *
   * @return a copy of the owner
   */
  public Member getOwner() {
    return new Member(owner);
  }

  /**
   * Gets the contracts.
   *
   * @return an unmodifiable list of contracts
   */
  public List<Contract> getContracts() {
    return Collections.unmodifiableList(contracts);
  }

  /**
   * Removes a contract.
   *
   * @param contract the contract to remove
   */
  public void removeContracts(Contract contract) {
    if (!contracts.remove(contract)) {
      throw new IllegalArgumentException("Contract not found");
    }
  }

  /**
   * Sets the item name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("The name cannot be empty or have no value at all.");
    }
    this.name = name;
  }

  /**
   * Sets the item category.
   *
   * @param category the new category
   */
  public void setCategory(Category category) {
    this.category = Objects.requireNonNull(category, "Category required");
  }

  /**
   * Category enumeration for items.
   */
  public enum Category {
    TOOL, VEHICLE, GAME, TOY, SPORT, OTHER
  }
  
  /**
   * Generates a unique item ID.
   *
   * @return a unique 6-character alphanumeric ID
   */
  public static String generateUniqueId() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 6; i++) {
      sb.append(ALPHANUM.charAt(RAND.nextInt(ALPHANUM.length())));
    }
    return sb.toString();
  }

  /**
   * Sets the item description.
   *
   * @param description the new description
   */
  public void setDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      throw new IllegalArgumentException("Description cant be empty");
    }
    this.description = description;
  }

  /**
   * Sets the cost per day.
   *
   * @param costPerDay the new cost per day
   */
  public void setCostPerDay(double costPerDay) {
    if (costPerDay < 0) {
      throw new IllegalArgumentException("Cannot cost less than 0");
    }
    this.costPerDay = costPerDay;
  }
  
  /**
   * Method to check if the item is available for the given dates.
   *
   * @param startDate of the contract
   * @param endDate of the contract
   * @return true if the item is available, false otherwise
   */
  public boolean isAvailable(int startDate, int endDate) {
    for (Contract contract : contracts) {
      int contractStart = contract.getStartDay();
      int contractEnd = contract.getEndDay();     
      // Check for overlap
      if (startDate <= contractEnd && endDate >= contractStart) {
        return false;
      }
    }
    return true;
  }

  /**
   * Method to add a new contract to the list of contracts.
   *
   * @param contract the contract to be added
   */
  public void addContract(Contract contract) {
    if (contract != null) {
      this.contracts.add(contract);
    }
  }
}
