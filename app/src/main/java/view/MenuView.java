package view;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import model.entity.Contract;
import model.entity.Item;
import model.entity.Item.Category;
import model.entity.Member;

/**
 * MenuView class provides passive UI methods for the Stuff Lending System.
 * Pure view following MVC pattern - only handles input/output, no business logic.
 */
public class MenuView {
  private final Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);

  /**
   * Get user choice from menu. * * @return the user's menu choice
   */
  public int getUserChoice() {
    showMenu();
    return promptInt("Enter your choice:");
  }

  /**
   * Check if user wants to continue. * * @return true if continuing
   */
  public boolean continueSession() {
    System.out.println("\nPress Enter to continue...");
    sc.nextLine();
    return true;
  }

  /**
   * Show main menu options.
   */
  public void showMenu() {
    System.out.println("\n\t--- Main Menu ---");
    System.out.println("1. Add Member");
    System.out.println("2. List Members (Simple)");
    System.out.println("3. List Members (Verbose)");
    System.out.println("4. View Member Details");
    System.out.println("5. Edit Member");
    System.out.println("6. Delete Member");
    System.out.println("7. create Item");
    System.out.println("8. List Item");
    System.out.println("9. Edit Item");
    System.out.println("10. Delete Item");
    System.out.println("11. Create Contract");
    System.out.println("12. List Contracts");
    System.out.println("13. Advance Day");
    System.out.println("0. Exit");
  }

  public String promptMemberId() {
    return prompt("Enter Member ID: ");
  }

  /**
   * Updated information on the user details.
   *
   * @return new array of the updated info
   */
  public String[] promptMemberUpdate() {
    String id = prompt("Member ID to edit: ");
    String newName = prompt("New name: ");
    String newPhone = prompt("New phoneno: ");
    String newEmail = prompt("New email");
    return new String[] {
      id, newName, newPhone, newEmail
      };
  }

  /**
   * Display advance day result.
   *
   * @param oldDay the previous day
   * @param newDay the new day
   */
  public void displayAdvanceDay(int oldDay, int newDay) {
    System.out.println("Day advanced from " + oldDay + " to " + newDay);
  }

  /**
   * Display exit message.
   */
  public void displayExitMessage() {
    System.out.println("Thank you for using Stuff Lending System. Goodbye!");
  }

  /**
   * Display invalid choice message.
   */
  public void displayInvalidChoice() {
    System.out.println("Invalid choice. Please try again.");
  }

  /**
   * Reads a line of txt after showing prompt.
   *
   * @param msg contains the txt.
   * @return trimmed input from the user.
   */
  public String prompt(String msg) {
    System.out.print(msg + " ");
    return sc.nextLine().trim();
  }

  /**
   * Reads an int after the prompt has been shown.
   *
   * @param msg stores the msg from the user.
   * @return int value.
   */
  public int promptInt(String msg) {
    while (true) {
      System.out.print(msg + " ");
      String line = sc.nextLine().trim();
      try {
        return Integer.parseInt(line);
      } catch (NumberFormatException e) {
        System.out.println("Invalid number. Please try again.");
      }
    }
  }

  /**
   * This reads a double after showing prompt (would retry on bad inputs).
   */
  public double promptDouble(String msg) {
    while (true) {
      System.out.print(msg + " ");
      String line = sc.nextLine().trim();
      try {
        return Double.parseDouble(line);
      } catch (NumberFormatException e) {
        System.out.println("Invalid decimal number, please try again.");
      }
    }
  }

  /**
   * Read and parse category enum.
   */
  public Category promptCategory(String msg) {
    while (true) {
      System.out.print(msg + " ");
      String line = sc.nextLine().trim().toUpperCase();
      try {
        return Category.valueOf(line);
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid category. Valid options: TOOL, VEHICLE, GAME, TOY, SPORT, OTHER");
      }
    }
  }

  /**
   * Display member details.
   *
   * @param member the member to display
   */
  public void displayMember(Member member) {
    System.out.println("Member ID: " + member.getMemberId());
    System.out.println("Name: " + member.getName());
    System.out.println("Email: " + member.getEmail());
    System.out.println("Phone: " + member.getPhone());
    System.out.println("Credits: " + member.getCredit());
    System.out.println("Created: Day " + member.getCreatedDate());
    System.out.println("Owned Items: " + member.getOwnedItems().size());
  }

  /**
   * Display member details verbose.
   *
   * @param memberDetails the detailed member information
   */
  public void displayMemberDetails(String memberDetails) {
    System.out.println(memberDetails);
  }

  /**
   * Display item details.
   *
   * @param item the item to display
   */
  public void displayItemDetails(Item item) {
    System.out.println("Item ID: " + item.getItemId());
    System.out.println("Name: " + item.getName());
    System.out.println("Category: " + item.getCategory());
    System.out.println("Description: " + item.getDescription());
    System.out.println("Cost/day: " + item.getCostPerDay());
    System.out.println("Owner: " + item.getOwner().getName());
    System.out.println("Created: Day " + item.getCreationDate());
    System.out.println("Available: Currently not checked (would need date range)");
  }

  /**
   * Display item with contract details.
   *
   * @param item      the item to display
   * @param contracts the contracts for this item
   */
  public void displayItemWithContracts(Item item, List<Contract> contracts) {
    displayItemDetails(item);
    System.out.println("\nContract History:");
    if (contracts.isEmpty()) {
      System.out.println("No contracts found for this item.");
    } else {
      for (Contract contract : contracts) {
        System.out.println(
            "- Contract " + contract.getContractId()
                + ": Days " + contract.getStartDay()
                + "-" + contract.getEndDay()
                + ", Borrower: " + contract.getBorrower().getName()
                + ", Cost: " + contract.getTotalCost());
      }
    }
  }

  /**
   * Display items list.
   *
   * @param items the list of items to display
   */
  public void displayItems(List<Item> items) {
    if (items.isEmpty()) {
      System.out.println("No items found");
    } else {
      System.out.println("\n=== Items ===");
      for (Item item : items) {
        System.out.println(
            item.getName() + " (ID: " + item.getItemId() + ", Owner: " + item.getOwner().getName() + ", Category: "
                + item.getCategory() + ")");
      }
    }
  }

  /**
   * Showing off each contract detail.
   *
   * @param contract the contract to display
   */
  public void displayContractDetails(Contract contract) {
    System.out.println("Contract ID: " + contract.getContractId());
    System.out.println("Item: " + contract.getItem().getName());
    System.out.println("Borrower: " + contract.getBorrower().getName());
    System.out.println("Start Date: Day " + contract.getStartDay());
    System.out.println("End Date: Day " + contract.getEndDay());
    System.out.println("Total Cost: " + contract.getTotalCost());
    System.out.println("Valid: " + (contract.valid() ? "Yes" : "No"));
  }

  /**
   * Display the list of contracts.
   *
   * @param contracts the list of contracts to display
   */
  public void displayContracts(List<Contract> contracts) {
    if (contracts.isEmpty()) {
      System.out.println("No contracts found");
    } else {
      System.out.println("\n=== Contracts ===");
      for (Contract contract : contracts) {
        System.out.println("Contract " + contract.getContractId() + ": " + contract.getItem().getName() + " (Days "
            + contract.getStartDay() + "-" + contract.getEndDay() + ")");
      }
    }
  }

  /**
   * Display action result.
   *
   * @param success whether the action was successful
   * @param message the message to display
   */
  public void displayActionResult(boolean success, String message) {
    String prefix = success ? "[SUCCESS] " : "[ERROR] ";
    System.out.println(prefix + message);
  }

  /**
   * Display members list.
   *
   * @param members the list of members to display
   * @param verbose whether to show verbose details
   */
  public void displayMembers(List<Member> members, boolean verbose) {
    if (members.isEmpty()) {
      System.out.println("No members found");
      return;
    }
    System.out.println("\n=== Members ===");
    for (Member member : members) {
      if (verbose) {
        displayMember(member);
        System.out.println("---");
      } else {
        System.out.println(
            member.getName() + " (ID: " + member.getMemberId() + ", Credits: " + member.getCredit() + ")");
      }
    }
  }

  /**
   * Display error message.
   *
   * @param message the error message
   */
  public void displayError(String message) {
    System.out.println("[ERROR] " + message);
  }

  /**
   * Display success message.
   *
   * @param message the success message
   */
  public void displaySuccess(String message) {
    System.out.println("[SUCCESS] " + message);
  }
}