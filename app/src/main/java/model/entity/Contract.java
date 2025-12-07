package model.entity;

import java.security.SecureRandom;
import java.util.Objects;
import model.LendingCostStrategy;
import model.service.TimeService;

/**
 * Class Contract representing a lending agreement.
 */
public class Contract {
  private static final String ALPHANUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final SecureRandom RAND = new SecureRandom();
  
  private final String contractId; // id for the contract
  private final Member borrower; // member who is renting the item
  private final Item item; // item being rented
  private final int startDay; // start date of the contract
  private final int endDay; // end date of the contract
  private final LendingCostStrategy lendingCostStrategy;
  private final TimeService time;
  private final boolean isValid; // check to see if the contract is valid
  private final double totalCost; // total cost of the contract

  /**
   * Constructor for the Contract class.
   *
   * @param borrower the member who is renting the item
   * @param item the item being rented
   * @param startDay the start date of the contract
   * @param endDay the end date of the contract
   * @param costStrategy the cost calculation strategy
   * @param time the time service
   */
  public Contract(Member borrower, Item item, int startDay, int endDay, 
                 LendingCostStrategy costStrategy, TimeService time) {
    this.contractId = generateUniqueId();
    this.borrower = Objects.requireNonNull(borrower, "Borrower required");
    this.item = Objects.requireNonNull(item, "Item required");
    this.startDay = startDay;
    this.endDay = endDay;
    this.lendingCostStrategy = Objects.requireNonNull(costStrategy);
    this.time = Objects.requireNonNull(time);
    this.isValid = validate();

    // compute cost and validation
    this.totalCost = isValid ? lendingCostStrategy.calcLendingCost(item, endDay - startDay + 1) : 0.0;
  }

  /**
   * Validates the contract conditions.
   *
   * @return true if the contract is valid
   */
  private boolean validate() {
    if (startDay < time.getCurrentDay()) {
      return false;
    }
    if (startDay > endDay) {
      return false;
    }
    if (!item.isAvailable(startDay, endDay)) {
      return false;
    }
    int days = endDay - startDay + 1;
    double cost = lendingCostStrategy.calcLendingCost(item, days);
    if (borrower.getCredit() < cost) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the contract is valid.
   *
   * @return true if valid
   */
  public boolean valid() {
    return isValid;
  }

  /**
   * Generates a unique contract ID.
   *
   * @return a unique 6-character alphanumeric ID
   */
  private String generateUniqueId() {
    StringBuilder sb = new StringBuilder(6);
    for (int i = 0; i < 6; i++) {
      sb.append(ALPHANUM.charAt(RAND.nextInt(ALPHANUM.length())));
    }
    return sb.toString();
  }

  /**
   * Checks if the item has been returned.
   *
   * @return true if returned
   */
  public boolean isReturned() {
    return time.getCurrentDay() > endDay;
  }

  /**
   * Gets the contract ID.
   *
   * @return the contract ID
   */
  public String getContractId() {
    return contractId;
  }

  /**
   * Gets the start day.
   *
   * @return the start day
   */
  public int getStartDay() {
    return startDay;
  }

  /**
   * Gets the end day.
   *
   * @return the end day
   */
  public int getEndDay() {
    return endDay;
  }

  /**
   * Gets the total cost.
   *
   * @return the total cost
   */
  public double getTotalCost() {
    return totalCost;
  }

  /**
   * Gets the borrower.
   *
   * @return a copy of the borrower
   */
  public Member getBorrower() {
    return new Member(borrower);
  }

  /**
   * Gets the item.
   *
   * @return a copy of the item
   */
  public Item getItem() {
    return new Item(item);
  }
}

