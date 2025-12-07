package model;

import model.service.MemberService;

/**
 * An in-memory implementation of the PersistenceInterface.
 * This class loads hardcoded member and item data for testing purposes.
 * It simulates data loading and saving without actual file or database persistence.
 */
public class MemoryPersistence implements PersistenceInterface {

  // private MemberService memberService;

  public MemoryPersistence(MemberService memberService) {
    // this.memberService = memberService;
  }

  @Override
  public void loadData() {
    // Member member1 = memberService.createMember("John Doe", "1234567", "john@exempel.com");
    // Member member2 = memberService.createMember("Jane Doe", "123423567", "jane@exempel.com");

    // member1.addItem(new Item());
    // member2.addItem(new Item());


  }

  @Override
  public void saveData() {
    System.out.println("Data Saving not implemented.");
  }
  
}
