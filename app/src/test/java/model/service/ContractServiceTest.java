package model.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.FlatRateStrategy;
import model.LendingCostStrategy;
import model.entity.Contract;
import model.entity.Item;
import model.entity.Member;
import model.persistence.DataStore;
import model.persistence.InMemoryDataStore;

/**
 * Unit tests for ContractService - focusing on atomic credit transfers.
 */
public class ContractServiceTest {
  private ContractService contractService;
  private TimeService timeService;
  private DataStore dataStore;
  private Member borrower;
  private Member owner;
  private Item item;
  private LendingCostStrategy strategy;

  @BeforeEach
  void setUp() {
    timeService = new TimeService();
    dataStore = new InMemoryDataStore();
    contractService = new ContractService(timeService, dataStore);
    
    // Create owner and item
    owner = new Member("Owner", "owner@example.com", "111-111-1111", timeService.getCurrentDay());
    owner.setCredit(0.0); // Start with 0 credits
    
    // Create borrower with sufficient credits - need enough for contract validation
    borrower = new Member("Borrower", "borrower@example.com", "222-222-2222", timeService.getCurrentDay());
    borrower.setCredit(200.0); // Increased to ensure sufficient credits for validation
    
    // Create item
    item = new Item("Test Item", Item.Category.TOOL, "Test description", 
                   10.0, owner, timeService);
    
    strategy = new FlatRateStrategy();
  }

  @Test
  void testSuccessfulContractCreation() throws Exception {
    double borrowerInitialCredit = borrower.getCredit(); // 200.0
    double ownerInitialCredit = owner.getCredit(); // 0.0
    
    int startDay = timeService.getCurrentDay();
    int endDay = startDay + 2; // 3 days total
    double expectedCost = 30.0; // 3 days * 10.0 per day
    
    Contract contract = contractService.createContract(borrower, owner, item, startDay, endDay, strategy);
    
    assertNotNull(contract);
    assertTrue(contract.valid());
    assertEquals(expectedCost, contract.getTotalCost());
    
    // Verify atomic credit transfer
    assertEquals(borrowerInitialCredit - expectedCost, borrower.getCredit()); // 200 - 30 = 170
    assertEquals(ownerInitialCredit + expectedCost, owner.getCredit()); // 0 + 30 = 30
    
    // Verify contract is registered with item
    assertTrue(item.getContracts().contains(contract));
  }

  @Test
  void testInsufficientCreditRollback() {
    // Set borrower credit insufficient for the contract
    borrower.setCredit(10.0); // Only 10 credits, but need 30
    double borrowerInitialCredit = borrower.getCredit();
    double ownerInitialCredit = owner.getCredit();
    
    int startDay = timeService.getCurrentDay();
    int endDay = startDay + 2; // 3 days, costs 30 credits
    
    // Contract creation should fail
    assertThrows(IllegalArgumentException.class, () -> {
      contractService.createContract(borrower, owner, item, startDay, endDay, strategy);
    });
    
    // Verify no credit changes occurred (rollback successful)
    assertEquals(borrowerInitialCredit, borrower.getCredit());
    assertEquals(ownerInitialCredit, owner.getCredit());
    
    // Verify no contract was added to item
    assertTrue(item.getContracts().isEmpty());
  }

  @Test
  void testUnavailableItemRollback() throws Exception {
    // Create an existing contract that overlaps
    int existingStartDay = timeService.getCurrentDay();
    int existingEndDay = existingStartDay + 3;
    
    Member otherBorrower = new Member("Other", "other@example.com", "333-333-3333", timeService.getCurrentDay());
    otherBorrower.setCredit(100.0); // Sufficient for validation
    
    // Create first contract successfully
    contractService.createContract(otherBorrower, owner, item, existingStartDay, existingEndDay, strategy);
    
    // Now try to create overlapping contract
    double borrowerInitialCredit = borrower.getCredit(); // 200.0
    double ownerInitialCredit = owner.getCredit(); // Should be 40.0 from previous contract
    
    int conflictStartDay = existingStartDay + 1; // Overlaps with existing
    int conflictEndDay = conflictStartDay + 2;
    
    // Contract creation should fail due to unavailability
    assertThrows(IllegalArgumentException.class, () -> {
      contractService.createContract(borrower, owner, item, conflictStartDay, conflictEndDay, strategy);
    });
    
    // Verify no additional credit changes occurred
    assertEquals(borrowerInitialCredit, borrower.getCredit());
    assertEquals(ownerInitialCredit, owner.getCredit());
    
    // Verify only one contract exists (the first one)
    assertEquals(1, item.getContracts().size());
  }

  @Test
  void testPastDateValidation() {
    double borrowerInitialCredit = borrower.getCredit();
    double ownerInitialCredit = owner.getCredit();
    
    // Try to create contract in the past
    int pastDay = timeService.getCurrentDay() - 1;
    int endDay = pastDay + 2;
    
    assertThrows(IllegalArgumentException.class, () -> {
      contractService.createContract(borrower, owner, item, pastDay, endDay, strategy);
    });
    
    // Verify no credit changes occurred
    assertEquals(borrowerInitialCredit, borrower.getCredit());
    assertEquals(ownerInitialCredit, owner.getCredit());
  }

  @Test
  void testInvalidDateRangeValidation() {
    double borrowerInitialCredit = borrower.getCredit();
    double ownerInitialCredit = owner.getCredit();
    
    // Try to create contract where start > end
    int startDay = timeService.getCurrentDay() + 5;
    int endDay = startDay - 1; // Invalid: end before start
    
    assertThrows(IllegalArgumentException.class, () -> {
      contractService.createContract(borrower, owner, item, startDay, endDay, strategy);
    });
    
    // Verify no credit changes occurred
    assertEquals(borrowerInitialCredit, borrower.getCredit());
    assertEquals(ownerInitialCredit, owner.getCredit());
  }

  @Test
  void testMultipleSuccessfulContracts() throws Exception {
    // Create multiple non-overlapping contracts to test credit accumulation
    
    // First contract
    int start1 = timeService.getCurrentDay();
    int end1 = start1 + 1; // 2 days, cost = 20
    Contract contract1 = contractService.createContract(borrower, owner, item, start1, end1, strategy);
    
    assertEquals(180.0, borrower.getCredit()); // 200 - 20
    assertEquals(20.0, owner.getCredit()); // 0 + 20
    
    // Second contract (non-overlapping)
    Member borrower2 = new Member("Borrower2", "borrower2@example.com", "444-444-4444", timeService.getCurrentDay());
    borrower2.setCredit(100.0); // Sufficient for contract validation
    
    int start2 = end1 + 1; // After first contract
    int end2 = start2 + 0; // 1 day, cost = 10
    Contract contract2 = contractService.createContract(borrower2, owner, item, start2, end2, strategy);
    
    assertEquals(90.0, borrower2.getCredit()); // 100 - 10
    assertEquals(30.0, owner.getCredit()); // 20 + 10
    
    // Verify both contracts exist
    assertEquals(2, item.getContracts().size());
    assertTrue(item.getContracts().contains(contract1));
    assertTrue(item.getContracts().contains(contract2));
  }
}
