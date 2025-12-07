package model;

import model.entity.Item;

/**
 * Flat rate implementation of the lending cost strategy.
 */
public class FlatRateStrategy implements LendingCostStrategy {
  @Override
  public double calcLendingCost(Item item, int days) {
    return item.getCostPerDay() * days;
  }
}
