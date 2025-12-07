package model.persistence;

import java.util.ArrayList;
import java.util.List;
import model.entity.Contract;
import model.entity.Item;
import model.entity.Member;

/**
 * In-memory implementation of the data store interface.
 * Stores data in memory during application runtime.
 */
public class InMemoryDataStore implements DataStore {
  
  // In-memory storage collections
  private final List<Member> members = new ArrayList<>();
  private final List<Item> items = new ArrayList<>();
  private final List<Contract> contracts = new ArrayList<>();
  
  @Override
  public List<Member> loadMembers() {
    // Return a copy to prevent external modification
    return new ArrayList<>(members);
  }

  @Override
  public void saveMembers(List<Member> members) {
    // Clear existing data and store new data
    this.members.clear();
    if (members != null) {
      this.members.addAll(members);
    }
  }

  @Override
  public List<Item> loadItems() {
    // Return a copy to prevent external modification
    return new ArrayList<>(items);
  }

  @Override
  public void saveItems(List<Item> items) {
    // Clear existing data and store new data
    this.items.clear();
    if (items != null) {
      this.items.addAll(items);
    }
  }

  @Override
  public List<Contract> loadContracts() {
    // Return a copy to prevent external modification
    return new ArrayList<>(contracts);
  }

  @Override
  public void saveContracts(List<Contract> contracts) {
    // Clear existing data and store new data
    this.contracts.clear();
    if (contracts != null) {
      this.contracts.addAll(contracts);
    }
  }
}
