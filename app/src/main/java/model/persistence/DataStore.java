package model.persistence;

import java.util.List;
import model.entity.Contract;
import model.entity.Item;
import model.entity.Member;

/**
 * Data store interface for persistence operations.
 */
public interface DataStore {
  /**
   * Loads members from storage.
   *
   * @return list of members
   */
  List<Member> loadMembers();

  /**
   * Saves members to storage.
   *
   * @param members the members to save
   */
  void saveMembers(List<Member> members);

  /**
   * Loads items from storage.
   *
   * @return list of items
   */
  List<Item> loadItems();

  /**
   * Saves items to storage.
   *
   * @param items the items to save
   */
  void saveItems(List<Item> items);

  /**
   * Loads contracts from storage.
   *
   * @return list of contracts
   */
  List<Contract> loadContracts();

  /**
   * Saves contracts to storage.
   *
   * @param contracts the contracts to save
   */
  void saveContracts(List<Contract> contracts);
}
