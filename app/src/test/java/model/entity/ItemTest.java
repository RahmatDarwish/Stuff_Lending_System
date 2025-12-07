package model.entity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.FlatRateStrategy;
import model.LendingCostStrategy;
import model.service.TimeService;

/**
 * Unit tests for Item entity - focusing on availability checking.
 */
public class ItemTest {
  private TimeService timeService;
  private Member owner;
  private Item item;
  private LendingCostStrategy strategy;

  @BeforeEach
  void setUp() {
    timeService = new TimeService();
    owner = new Member("Owner", "owner@example.com", "111-111-1111", timeService.getCurrentDay());
    item = new Item("Test Item", Item.Category.TOOL, "Test description", 10.0, owner, timeService);
    strategy = new FlatRateStrategy();
  }

  @Test
  void testItemCreation() {
    assertNotNull(item);
    assertEquals("Test Item", item.getName());
    assertEquals(Item.Category.TOOL, item.getCategory());
    assertEquals("Test description", item.getDescription());
    assertEquals(10.0, item.getCostPerDay());
    
    // Owner returns a copy, so compare by ID instead
    assertEquals(owner.getMemberId(), item.getOwner().getMemberId());
    assertEquals(owner.getName(), item.getOwner().getName());
    
    assertEquals(6, item.getItemId().length());
    assertTrue(item.getItemId().matches("[A-Z0-9]{6}"));
  }

  @Test
  void testInitialAvailability() {
    // Item should be available for any date range initially
    int currentDay = timeService.getCurrentDay();
    assertTrue(item.isAvailable(currentDay, currentDay + 10));
    assertTrue(item.isAvailable(currentDay + 5, currentDay + 15));
  }

  @Test
  void testAvailabilityWithContract() {
    // Add a contract for days 5-10
    int startDay = timeService.getCurrentDay() + 5;
    int endDay = timeService.getCurrentDay() + 10;
    
    // Create a borrower with sufficient credits for the contract
    Member contractBorrower = new Member("Contract Borrower", "borrower@test.com", "555-555-5555", timeService.getCurrentDay());
    contractBorrower.setCredit(100.0);
    
    Contract contract = new Contract(contractBorrower, item, startDay, endDay, strategy, timeService);
    item.addContract(contract);
    
    // Should not be available during contract period
    assertFalse(item.isAvailable(startDay, endDay));
    assertFalse(item.isAvailable(startDay + 1, endDay - 1)); // Subset
    assertFalse(item.isAvailable(startDay - 1, startDay + 1)); // Overlap start
    assertFalse(item.isAvailable(endDay - 1, endDay + 1)); // Overlap end
    assertFalse(item.isAvailable(startDay - 2, endDay + 2)); // Superset
    
    // Should be available before and after
    assertTrue(item.isAvailable(timeService.getCurrentDay(), startDay - 1));
    assertTrue(item.isAvailable(endDay + 1, endDay + 5));
  }

  @Test
  void testAvailabilityWithMultipleContracts() {
    // Add multiple non-overlapping contracts
    int currentDay = timeService.getCurrentDay();
    
    // Create borrowers with sufficient credits
    Member borrower1 = new Member("Borrower1", "b1@test.com", "111-111-1111", currentDay);
    borrower1.setCredit(100.0);
    Member borrower2 = new Member("Borrower2", "b2@test.com", "222-222-2222", currentDay);
    borrower2.setCredit(100.0);
    
    // Contract 1: days 5-7
    Contract contract1 = new Contract(borrower1, item, currentDay + 5, currentDay + 7, strategy, timeService);
    item.addContract(contract1);
    
    // Contract 2: days 10-12
    Contract contract2 = new Contract(borrower2, item, currentDay + 10, currentDay + 12, strategy, timeService);
    item.addContract(contract2);
    
    // Should not be available during either contract period
    assertFalse(item.isAvailable(currentDay + 5, currentDay + 7));
    assertFalse(item.isAvailable(currentDay + 10, currentDay + 12));
    assertFalse(item.isAvailable(currentDay + 6, currentDay + 11)); // Spans both
    
    // Should be available in gaps
    assertTrue(item.isAvailable(currentDay, currentDay + 4)); // Before first
    assertTrue(item.isAvailable(currentDay + 8, currentDay + 9)); // Between contracts
    assertTrue(item.isAvailable(currentDay + 13, currentDay + 15)); // After last
  }

  @Test
  void testExactBoundaryAvailability() {
    int currentDay = timeService.getCurrentDay();
    
    Member borrower = new Member("Borrower", "borrower@test.com", "333-333-3333", currentDay);
    borrower.setCredit(100.0);
    
    // Contract for days 5-10
    Contract contract = new Contract(borrower, item, currentDay + 5, currentDay + 10, strategy, timeService);
    item.addContract(contract);
    
    // Exact boundary checks
    assertTrue(item.isAvailable(currentDay + 3, currentDay + 4)); // Ends day before
    assertTrue(item.isAvailable(currentDay + 11, currentDay + 12)); // Starts day after
    assertFalse(item.isAvailable(currentDay + 4, currentDay + 5)); // Overlaps start
    assertFalse(item.isAvailable(currentDay + 10, currentDay + 11)); // Overlaps end
  }

  @Test
  void testSingleDayContracts() {
    int currentDay = timeService.getCurrentDay();
    
    Member borrower = new Member("Borrower", "borrower@test.com", "444-444-4444", currentDay);
    borrower.setCredit(100.0);
    
    // Single day contract on day 5
    Contract contract = new Contract(borrower, item, currentDay + 5, currentDay + 5, strategy, timeService);
    item.addContract(contract);
    
    // Should not be available on that exact day
    assertFalse(item.isAvailable(currentDay + 5, currentDay + 5));
    
    // Should be available on adjacent days
    assertTrue(item.isAvailable(currentDay + 4, currentDay + 4));
    assertTrue(item.isAvailable(currentDay + 6, currentDay + 6));
  }

  @Test
  void testItemUniqueIdGeneration() {
    Item item2 = new Item("Another Item", Item.Category.VEHICLE, "Another description", 
                         20.0, owner, timeService);
    
    assertNotEquals(item.getItemId(), item2.getItemId());
    assertEquals(6, item2.getItemId().length());
    assertTrue(item2.getItemId().matches("[A-Z0-9]{6}"));
  }

  @Test
  void testCostValidation() {
    assertThrows(IllegalArgumentException.class, () -> {
      item.setCostPerDay(-1.0);
    });
    
    // Valid cost should work
    item.setCostPerDay(15.0);
    assertEquals(15.0, item.getCostPerDay());
  }

  @Test
  void testContractRemoval() {
    int currentDay = timeService.getCurrentDay();
    
    Member borrower = new Member("Borrower", "borrower@test.com", "555-555-5555", currentDay);
    borrower.setCredit(100.0);
    
    Contract contract = new Contract(borrower, item, currentDay + 5, currentDay + 7, strategy, timeService);
    
    item.addContract(contract);
    assertFalse(item.isAvailable(currentDay + 5, currentDay + 7));
    
    item.removeContracts(contract);
    assertTrue(item.isAvailable(currentDay + 5, currentDay + 7));
  }

  @Test
  void testInvalidContractRemoval() {
    Member borrower = new Member("Borrower", "borrower@test.com", "666-666-6666", timeService.getCurrentDay());
    borrower.setCredit(100.0);
    
    Contract contract = new Contract(borrower, item, timeService.getCurrentDay() + 5, 
                                   timeService.getCurrentDay() + 7, strategy, timeService);
    
    // Try to remove contract that was never added
    assertThrows(IllegalArgumentException.class, () -> {
      item.removeContracts(contract);
    });
  }
}
