package model.entity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.service.TimeService;

/**
 * Unit tests for Member entity.
 */
public class MemberTest {
  private TimeService timeService;
  private Member member;

  @BeforeEach
  void setUp() {
    timeService = new TimeService();
    member = new Member("John Doe", "john@example.com", "123-456-7890", timeService.getCurrentDay());
  }

  @Test
  void testMemberCreation() {
    assertNotNull(member);
    assertEquals("John Doe", member.getName());
    assertEquals("123-456-7890", member.getPhone());
    assertEquals("john@example.com", member.getEmail());
    assertEquals(6, member.getMemberId().length());
    assertEquals(0.0, member.getCredit());
  }

  @Test
  void testUniqueIdGeneration() {
    Member member2 = new Member("Jane Smith", "jane@example.com", "987-654-3210", timeService.getCurrentDay());
    assertNotEquals(member.getMemberId(), member2.getMemberId());
    assertTrue(member.getMemberId().matches("[A-Z0-9]{6}"));
    assertTrue(member2.getMemberId().matches("[A-Z0-9]{6}"));
  }

  @Test
  void testCreditOperations() {
    // Test increase credit
    member.increaseCredit(100.0);
    assertEquals(100.0, member.getCredit());

    // Test deduct credit
    member.deductCredit(30.0);
    assertEquals(70.0, member.getCredit());

    // Test set credit
    member.setCredit(50.0);
    assertEquals(50.0, member.getCredit());
  }

  @Test
  void testInsufficientCreditDeduction() {
    member.setCredit(20.0);
    assertThrows(IllegalArgumentException.class, () -> {
      member.deductCredit(30.0);
    });
  }

  @Test
  void testNegativeCreditOperations() {
    assertThrows(IllegalArgumentException.class, () -> {
      member.increaseCredit(-10.0);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      member.setCredit(-5.0);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      member.deductCredit(-15.0);
    });
  }

  @Test
  void testItemRegistrationCreditAward() {
    Item item = new Item("Test Item", Item.Category.TOOL, "Test description", 
                        10.0, member, timeService);
    
    // Member should have 0 credits initially
    assertEquals(0.0, member.getCredit());
    
    // Adding item should award +100 credits
    member.addItem(item);
    assertEquals(100.0, member.getCredit());
  }

  @Test
  void testMultipleItemRegistrations() {
    Item item1 = new Item("Item 1", Item.Category.TOOL, "Description 1", 
                         10.0, member, timeService);
    Item item2 = new Item("Item 2", Item.Category.VEHICLE, "Description 2", 
                         20.0, member, timeService);
    
    member.addItem(item1);
    assertEquals(100.0, member.getCredit());
    
    member.addItem(item2);
    assertEquals(200.0, member.getCredit());
  }

  @Test
  void testNullParameterValidation() {
    assertThrows(NullPointerException.class, () -> {
      new Member(null, "john@example.com", "123-456-7890", timeService.getCurrentDay());
    });

    assertThrows(NullPointerException.class, () -> {
      new Member("John Doe", null, "123-456-7890", timeService.getCurrentDay());
    });

    assertThrows(NullPointerException.class, () -> {
      new Member("John Doe", "john@example.com", null, timeService.getCurrentDay());
    });
  }
}
