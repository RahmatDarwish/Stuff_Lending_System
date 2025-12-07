package model.persistence;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import model.entity.Member;
import org.junit.jupiter.api.Test;

/**
 * Test class for InMemoryDataStore to verify persistence functionality.
 */
public class InMemoryDataStoreTest {
  
  @Test
  public void testMemberPersistence() {
    InMemoryDataStore dataStore = new InMemoryDataStore();
    
    // Initially should be empty
    assertTrue(dataStore.loadMembers().isEmpty());
    
    // Create test members
    Member member1 = new Member("John", "john@test.com", "123456", 0);
    Member member2 = new Member("Jane", "jane@test.com", "789012", 0);
    List<Member> members = Arrays.asList(member1, member2);
    
    // Save members
    dataStore.saveMembers(members);
    
    // Load and verify
    List<Member> loadedMembers = dataStore.loadMembers();
    assertEquals(2, loadedMembers.size());
    assertEquals("John", loadedMembers.get(0).getName());
    assertEquals("Jane", loadedMembers.get(1).getName());
    
    // Verify defensive copying (modifications to returned list don't affect storage)
    loadedMembers.clear();
    List<Member> reloadedMembers = dataStore.loadMembers();
    assertEquals(2, reloadedMembers.size()); // Should still have 2 members
  }
  
  @Test
  public void testSaveNullMembers() {
    InMemoryDataStore dataStore = new InMemoryDataStore();
    
    // Save null should not throw exception
    assertDoesNotThrow(() -> dataStore.saveMembers(null));
    
    // Should return empty list
    assertTrue(dataStore.loadMembers().isEmpty());
  }
  
  @Test
  public void testMultipleSaves() {
    InMemoryDataStore dataStore = new InMemoryDataStore();
    
    // First save
    Member member1 = new Member("John", "john@test.com", "123456", 0);
    dataStore.saveMembers(Arrays.asList(member1));
    assertEquals(1, dataStore.loadMembers().size());
    
    // Second save should replace, not append
    Member member2 = new Member("Jane", "jane@test.com", "789012", 0);
    dataStore.saveMembers(Arrays.asList(member2));
    
    List<Member> members = dataStore.loadMembers();
    assertEquals(1, members.size());
    assertEquals("Jane", members.get(0).getName());
  }
}
