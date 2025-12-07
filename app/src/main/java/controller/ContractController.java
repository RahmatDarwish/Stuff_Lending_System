package controller;

import java.util.List;
import model.FlatRateStrategy;
import model.LendingCostStrategy;
import model.entity.Contract;
import model.entity.Item;
import model.entity.Member;
import model.service.ContractService;
import model.service.ItemService;
import model.service.MemberService;
import view.MenuView;

/**
 * Controller for managing contract operations.
 * Delegates all business logic to ContractService.
 */
public class ContractController {
  private final ContractService contractService;
  private final MemberService memberService;
  private final ItemService itemService;

  /**
   * Constructor for ContractController.
   *
   * @param contractService the service to handle contract business logic
   * @param memberService the service to find members
   * @param itemService the service to find items
   */
  public ContractController(ContractService contractService, MemberService memberService, 
                           ItemService itemService) {
    this.contractService = contractService;
    this.memberService = memberService;
    this.itemService = itemService;
  }

  /**
   * Handle create contract from UI.
   *
   * @param view the view to interact with
   */
  public void handleCreateContract(MenuView view) {
    try {
      String borrowerId = view.prompt("Enter borrower member ID:");
      Member borrower = memberService.findMemberById(borrowerId);
      if (borrower == null) {
        view.displayError("Borrower not found with ID: " + borrowerId);
        return;
      }
      
      String itemId = view.prompt("Enter item ID:");
      Item item = itemService.findItemById(itemId);
      if (item == null) {
        view.displayError("Item not found with ID: " + itemId);
        return;
      }
      
      // Get the actual owner from member service.
      Member ownerCopy = item.getOwner();
      Member owner = memberService.findMemberById(ownerCopy.getMemberId());
      
      int startDay = view.promptInt("Enter start day:");
      int endDay = view.promptInt("Enter end day:");
      
      // Use flat rate strategy for simplicity
      LendingCostStrategy strategy = new FlatRateStrategy();
      
      Contract contract = createContract(borrower, owner, item, startDay, endDay, strategy);
      view.displaySuccess("Contract created successfully!");
      view.displayContractDetails(contract);
    } catch (Exception e) {
      view.displayError("Failed to create contract: " + e.getMessage());
    }
  }

  /**
   * Handle list contracts from UI.
   *
   * @param view the view to display results
   */
  public void handleListContracts(MenuView view) {
    try {
      List<Contract> contracts = getAllContracts();
      view.displayContracts(contracts);
    } catch (Exception e) {
      view.displayError("Failed to list contracts: " + e.getMessage());
    }
  }

  /**
   * Creates a new contract by delegating to the service.
   *
   * @param borrower the member borrowing the item
   * @param owner the member who owns the item
   * @param item the item being borrowed
   * @param startDay the start day of the contract
   * @param endDay the end day of the contract
   * @param strategy the cost calculation strategy
   * @return the created contract
   * @throws Exception if contract creation fails
   */
  public Contract createContract(Member borrower, Member owner, Item item, int startDay, int endDay, 
                                LendingCostStrategy strategy) throws Exception {
    return contractService.createContract(borrower, owner, item, startDay, endDay, strategy);
  }

  /**
   * Gets all contracts.
   *
   * @return list of all contracts
   */
  public List<Contract> getAllContracts() {
    return contractService.getAllContracts();
  }
}