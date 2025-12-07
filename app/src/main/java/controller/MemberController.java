package controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import model.entity.Member;
import model.service.MemberService;
import view.MenuView;

/**
 * Controller handling member related operations.
 */
public class MemberController {
  private final MemberService memberService;

  /**
   * Constructor.
   *
   * @param memberService the service to handle member business logic
   */
  public MemberController(MemberService memberService) {
    if (memberService == null) {
      throw new IllegalArgumentException("memberService must not be null");
    }
    this.memberService = memberService;
  }

  /**
   * Handle add member from UI.
   *
   * @param view the view to interact with
   */
  public void handleAddMember(MenuView view) {
    try {
      String name = view.prompt("Enter member name:");
      String phone = view.prompt("Enter phone number:");
      String email = view.prompt("Enter email:");
      
      Member member = createMember(name, phone, email);
      view.displaySuccess("Member created successfully! ID: " + member.getMemberId());
    } catch (Exception e) {
      view.displayError("Failed to create member: " + e.getMessage());
    }
  }

  /**
   * Handle list members from UI.
   *
   * @param view the view to display results
   * @param verbose whether to show detailed information
   */
  public void handleListMembers(MenuView view, boolean verbose) {
    Collection<Member> members = listAllMembers();
    List<Member> memberList = new ArrayList<>(members);
    view.displayMembers(memberList, verbose);
  }
  
  /**
   * Simple ui message to dispaly user details.
   *
   * @param view instantiating menuview.
   */
  public void handleViewMemberDetails(MenuView view) {
    try {
      String id = view.promptMemberId();
      String info = viewMemberDetails(id);
      System.out.println("\n=== Member details ===\n" + info);
    } catch (Exception e) {
      view.displayError("Failed to view member: " + e.getMessage());
    }
  }

  /**
   * Method to view member Details.
   *
   * @param member the member to view details for
   * @return A string containing the members details.
   */
  public String viewMemberDetails(Member member) {
    return "Member ID: " + member.getMemberId()
            + "\nName: " + member.getName()
            + "\nEmail: " + member.getEmail()
            + "\nPhone number: " + member.getPhone()
            + "\nCredits: " + member.getCredit()
            + "\nOwned Items: " + member.getOwnedItems().size();
  }

  /**
   * Views member details by ID.
   *
   * @param memberId the ID of the member
   * @return string representation of member details
   */
  public String viewMemberDetails(String memberId) {
    Member m = memberService.findMemberById(memberId);
    if (m == null) {
      throw new IllegalArgumentException("No member with that ID: " + memberId);
    }
    return viewMemberDetails(m);
  }

  /**
   * Creates a new member.
   *
   * @param name the member's name
   * @param phone the member's phone
   * @param email the member's email
   * @return the created member
   */
  public Member createMember(String name, String phone, String email) {
    return memberService.createMember(name, phone, email);
  }

  /**
   * Deletes a member by ID.
   *
   * @param memberId the ID of the member to delete
   * @return true if deletion was successful
   */
  public boolean deleteMember(String memberId) {
    return memberService.deleteMemberById(memberId);
  }

  /**
   * Returns and deletes the specified member by its id.
   *
   * @param view instance of menuview class.
   */
  public void handleDeleteMember(MenuView view) {
    try {
      String id = view.promptMemberId();
      boolean ok = deleteMember(id);
      System.out.println(ok ? "Member deleted..." : "No member deleted (id not found)...");
    } catch (Exception e) {
      view.displayError("Failed to delete member: " + e.getMessage());
    }
  }

  /**
   * Updates a member's information.
   *
   * @param memberId the ID of the member to update
   * @param newName the new name
   * @param newPhoneNo the new phone number
   * @param newEmail the new email
   * @return the updated member
   */
  public Member updateMember(String memberId, String newName, String newPhoneNo, String newEmail) {
    return memberService.updateMember(memberId, newName, newPhoneNo, newEmail);
  }
  
  /**
   * This method handles specific member info and updates them.
   *
   * @param view instance of the ui menuview.
   *
   */
  public void handleUpdateMember(MenuView view) {
    try {
      String[] f = view.promptMemberUpdate();
      Member update = updateMember(f[0], f[1], f[2], f[3]);
      System.out.println("Member updated: " + update);
    } catch (IllegalArgumentException e) {
      System.out.println("Failed to update member: " + e.getMessage());
    }
  }

  /**
   * Gets all members.
   *
   * @return collection of all members
   */
  public Collection<Member> listAllMembers() {
    return memberService.getAllMembers();
  }
}
