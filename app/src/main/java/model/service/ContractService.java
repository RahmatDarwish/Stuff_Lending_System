package model.service;

import java.util.List;
import model.LendingCostStrategy;
import model.entity.Contract;
import model.entity.Item;
import model.entity.Member;
import model.persistence.DataStore;


/**
 * Service for managing contract operations with business logic.
 */

public class ContractService {

  private final TimeService clock;

  private final DataStore dataStore;


  /**
   * Constructor for ContractService.
   *
   * @param clock     the time service for getting current time
   * @param dataStore the data store for persistence
   */

  public ContractService(TimeService clock, DataStore dataStore) {

    this.clock = clock;

    this.dataStore = dataStore;

  }


  /**
   * Creates a contract with atomic credit transfer and rollback on failure.
   *
   * @param borrower     the member borrowing the item
   * @param owner        the member who owns the item
   * @param item         the item being borrowed
   * @param startDay     the start day of the contract
   * @param endDay       the end day of the contract
   * @param costStrategy the cost calculation strategy
   * @return the created contract
   * @throws Exception if contract creation fails
   */

  public Contract createContract(Member borrower, Member owner, Item item, int startDay, int endDay,

                                 LendingCostStrategy costStrategy) throws Exception {

    Contract contract = new Contract(borrower, item, startDay, endDay, costStrategy, clock);


    if (!contract.valid()) {

      throw new IllegalArgumentException("Cannot establish contract due to invalid conditions");

    }


    // Check if owner is reserving their own item - should be free

    double cost = borrower.equals(item.getOwner()) ? 0 : contract.getTotalCost();

    double borrowerOriginalCredit = borrower.getCredit();

    double ownerOriginalCredit = owner.getCredit();


    try {

      // Only perform credit transfer if there's a cost

      if (cost > 0) {

        // Step 1: Deduct from borrower

        borrower.deductCredit(cost);


        // Step 2: Add to owner

        owner.increaseCredit(cost);

      }


      // Step 3: Register contract with item (always done regardless of cost)

      item.addContract(contract);


      // Step 4: Persist contract to datastore

      List<Contract> contracts = dataStore.loadContracts();

      contracts.add(contract);

      dataStore.saveContracts(contracts);


      // Step 5: Persist member changes to datastore (critical for credit updates)

      List<Member> members = dataStore.loadMembers();

      // Update the members in the list with the modified credit values

      for (int i = 0; i < members.size(); i++) {

        Member storedMember = members.get(i);

        if (storedMember.getMemberId().equals(borrower.getMemberId())) {

          members.set(i, borrower);

        } else if (storedMember.getMemberId().equals(owner.getMemberId())) {

          members.set(i, owner);

        }

      }

      dataStore.saveMembers(members);


      return contract;

    } catch (Exception e) {

      // Rollback credit changes if any step fails

      borrower.setCredit(borrowerOriginalCredit);

      owner.setCredit(ownerOriginalCredit);

      throw new Exception("Contract creation failed: " + e.getMessage(), e);

    }

  }


  /**
   * Get all contracts from persistence.
   *
   * @return list of all contracts
   */

  public List<Contract> getAllContracts() {

    return dataStore.loadContracts();

  }

}