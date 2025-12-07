package controller;

import model.entity.Item;
import model.entity.Member;
import model.persistence.DataStore;
import model.persistence.InMemoryDataStore;
import model.service.ContractService;
import model.service.ItemService;
import model.service.MemberService;
import model.service.TimeService;
import view.MenuView;

/**
 * Responsible for starting the application.
 */

public class App {
  /**
   * Application starting point.
   *
   * @param args command line arguments.
   */
  public static void main(String[] args) {

    // Initialize services
    TimeService timeService = new TimeService();
    DataStore dataStore = new InMemoryDataStore();
    MemberService memberService = new MemberService(timeService, dataStore);
    ItemService itemService = new ItemService(dataStore, timeService, memberService);
    ContractService contractService = new ContractService(timeService, dataStore);

    // Initialize controllers

    MemberController memberController = new MemberController(memberService);
    final ItemController itemController = new ItemController(itemService);
    final ContractController contractController = new ContractController(contractService, memberService, itemService);

    // Initialize view

    MenuView menuView = new MenuView();
    // Demonstrate system functionality with sample data

    initializeSampleData(memberController, itemController, contractController, timeService);


    // Start interactive menu loop (controller manages the flow)

    System.out.println("\n=== Welcome to Stuff Lending System ===");

    runInteractiveMenu(menuView, memberController, itemController, contractController, timeService);

  }

  /**
   * Run the interactive menu loop with the controller in charge.
   *
   * @param view               the menu view
   * @param memberController   the member controller
   * @param itemController     the item controller
   * @param contractController the contract controller
   * @param timeService        the time service
   */

  private static void runInteractiveMenu(MenuView view, MemberController memberController,
      ItemController itemController, ContractController contractController,
      TimeService timeService) {

    boolean exit = false;

    while (!exit) {
      int choice = view.getUserChoice();
      switch (choice) {
        case 1:
          memberController.handleAddMember(view);
          break;
        case 2:
          memberController.handleListMembers(view, false); // Simple list
          break;
        case 3:
          memberController.handleListMembers(view, true); // Verbose list
          break;
        case 4:
          memberController.handleViewMemberDetails(view);
          break;
        case 5:
          memberController.handleUpdateMember(view);
          break;
        case 6:
          memberController.handleDeleteMember(view);
          break;
        case 7:
          itemController.handleCreateItem(view);
          break;
        case 8:
          itemController.handleListItems(view);
          break;
        case 9:
          itemController.handleEditItem(view);
          break;
        case 10:
          itemController.handleDeleteItem(view);
          break;
        case 11:
          contractController.handleCreateContract(view);
          break;
        case 12:
          contractController.handleListContracts(view);
          break;
        case 13:
          handleAdvanceDay(view, timeService);
          break;
        case 0:
          exit = true;
          view.displayExitMessage();
          break;
        default:
          view.displayInvalidChoice();
      }

      if (!exit) {
        view.continueSession();
      }
    }
  }

  /**
   * Handle advancing the day.
   *
   * @param view        the menu view
   * @param timeService the time service
   */

  private static void handleAdvanceDay(MenuView view, TimeService timeService) {

    int currentDay = timeService.getCurrentDay();

    timeService.advanceDay();

    int newDay = timeService.getCurrentDay();

    view.displayAdvanceDay(currentDay, newDay);

  }
  /**
   * Initialize sample data for demonstration.
   */

  private static void initializeSampleData(MemberController memberController,

      ItemController itemController,
      ContractController contractController,

      TimeService timeService) {
    
    try {
      // Create sample members
      Member member1 = memberController.createMember("John Doe", "1234567890", "john@example.com");
      Member member2 = memberController.createMember("Jane Smith", "0987654321", "jane@example.com");
      System.out.println("=== Sample Data Initialized ===");
      System.out.println("Members created: " + member1.getName() + ", " + member2.getName());
      
      // Create sample items
      Item item1 = itemController.createItem("Power Drill", Item.Category.TOOL,
          "A powerful cordless drill", 5.0, member1.getMemberId());
      Item item2 = itemController.createItem("Mountain Bike", Item.Category.SPORT,
          "High-quality mountain bike", 15.0, member2.getMemberId());

      System.out.println("Items created: " + item1.getName() + ", " + item2.getName());
      System.out.println("Current day: " + timeService.getCurrentDay());
    } catch (Exception e) {

      System.out.println("Warning: Could not initialize sample data - " + e.getMessage());

    }

  }

}