package model.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import model.FlatRateStrategy;
import model.LendingCostStrategy;
import model.entity.Contract;
import model.entity.Item;
import model.entity.Member;

/**
 * Simple debug test to understand Contract creation.
 */
public class DebugContractTest {

  @Test
  void debugContractCreation() {
    TimeService timeService = new TimeService();
    
    // Create owner and item
    Member owner = new Member("Owner", "owner@example.com", "111-111-1111", timeService.getCurrentDay());
    owner.setCredit(0.0);
    
    // Create borrower with sufficient credits
    Member borrower = new Member("Borrower", "borrower@example.com", "222-222-2222", timeService.getCurrentDay());
    borrower.setCredit(200.0);
    
    // Create item
    Item item = new Item("Test Item", Item.Category.TOOL, "Test description", 
                       10.0, owner, timeService);
    
    LendingCostStrategy strategy = new FlatRateStrategy();
    
    int startDay = timeService.getCurrentDay();
    int endDay = startDay + 2; // 3 days total
    
    System.out.println("Before contract creation:");
    System.out.println("Borrower credit: " + borrower.getCredit());
    System.out.println("Owner credit: " + owner.getCredit());
    System.out.println("Current day: " + timeService.getCurrentDay());
    System.out.println("Start day: " + startDay);
    System.out.println("End day: " + endDay);
    System.out.println("Item cost per day: " + item.getCostPerDay());
    System.out.println("Item available: " + item.isAvailable(startDay, endDay));
    
    // Create contract directly (not through service)
    Contract contract = new Contract(borrower, item, startDay, endDay, strategy, timeService);
    
    System.out.println("\nAfter contract creation:");
    System.out.println("Contract valid: " + contract.valid());
    System.out.println("Contract total cost: " + contract.getTotalCost());
    System.out.println("Expected cost: " + (3 * 10.0));
    
    // Let's test the strategy directly
    double strategyCost = strategy.calcLendingCost(item, 3);
    System.out.println("Strategy cost for 3 days: " + strategyCost);
    
    assertTrue(contract.valid());
    assertEquals(30.0, contract.getTotalCost());
  }
}
