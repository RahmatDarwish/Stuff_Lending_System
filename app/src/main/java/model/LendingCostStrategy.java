package model;

import model.entity.Item;

/**
 * Strategy interface for calculating lending costs.
 */
public interface LendingCostStrategy {
  /**
   * Calculates the lending cost for an item over a number of days.
   *
   * @param item the item being lent
   * @param days the number of days
   * @return the total cost
   */
  double calcLendingCost(Item item, int days);
}
