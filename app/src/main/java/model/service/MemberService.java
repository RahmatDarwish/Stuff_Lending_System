package model.service;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import model.entity.Member;
import model.persistence.DataStore;

/**
 * Service class for managing members.
 */
public class MemberService {
  private static final String ALPHANUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final SecureRandom RAND = new SecureRandom();
  private final TimeService timeService;
  private final DataStore dataStore;
  private Map<String, Member> members;
  private Set<String> usedIds;

  /**
   * Constructor to initialize the member collection.
   *
   * @param timeService the time service for creation dates
   * @param dataStore the data store for persistence
   */
  public MemberService(TimeService timeService, DataStore dataStore) {
    this.timeService = timeService;
    this.dataStore = dataStore;
    this.members = new HashMap<>();
    this.usedIds = new HashSet<>();
    loadMembers();
  }

  /**
   * Load members from data store into local cache.
   */
  private void loadMembers() {
    List<Member> storedMembers = dataStore.loadMembers();
    for (Member member : storedMembers) {
      members.put(member.getMemberId(), member);
      usedIds.add(member.getMemberId());
    }
  }

  /**
   * Save members to data store.
   */
  private void saveMembers() {
    List<Member> membersList = List.copyOf(members.values());
    dataStore.saveMembers(membersList);
  }

  /**
   * Generates a globally unique 6-character alphanumeric ID.
   *
   * @return a unique member ID
   */
  public String generateUniqueId() {
    String id;
    do {
      StringBuilder sb = new StringBuilder(6);
      for (int i = 0; i < 6; i++) {
        sb.append(ALPHANUM.charAt(RAND.nextInt(ALPHANUM.length())));
      }
      id = sb.toString();
    } while (usedIds.contains(id));
    usedIds.add(id);
    return id;
  }

  /**
   * Creates a Member with the given details.
   *
   * @param name Member's name
   * @param phone Member's phone
   * @param email Member's email
   * @return a new member
   */
  public Member createMember(String name, String phone, String email) {
    int createDay = timeService.getCurrentDay();
    if (!isUniqueEmail(email) || (!isUniquePhone(phone))) {
      throw new IllegalArgumentException("Email or phone is already in use.");
    }
    String memberId = generateUniqueId();
    Member newMember = new Member(memberId, name, email, phone, createDay);
    members.put(newMember.getMemberId(), newMember);
    saveMembers(); // Persist to datastore
    return newMember;
  }

  /**
   * Checks if email is unique.
   *
   * @param email the email to check
   * @return true if unique
   */
  private boolean isUniqueEmail(String email) {
    for (Member member : members.values()) {
      if (member.getEmail().equals(email)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if phone is unique.
   *
   * @param phone the phone to check
   * @return true if unique
   */
  private boolean isUniquePhone(String phone) {
    for (Member member : members.values()) {
      if (member.getPhone().equals(phone)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Finding members by their ID.
   *
   * @param memberId Member's ID
   * @return Member or null if not found
   */
  public Member findMemberById(String memberId) {
    return members.get(memberId);
  }

  /**
   * Deletes a member by ID.
   *
   * @param memberId the member ID to delete
   * @return true if deletion was successful
   */
  public boolean deleteMemberById(String memberId) {
    return members.remove(memberId) != null;
  }

  /**
   * Return an unmodifiable snapshot of all the members. * * @return the non-modified state of members
   */
  public Collection<Member> getAllMembers() {
    return Collections.unmodifiableCollection(members.values());
  }

  /**
   * Updates a member's information.
   *
   * @param memberId the member ID
   * @param newName the new name
   * @param newPhoneNo the new phone number
   * @param newEmail the new email
   * @return the updated member
   */
  public Member updateMember(String memberId, String newName, String newPhoneNo, String newEmail) {
    Member m = members.get(memberId);
    if (m == null) {
      throw new IllegalArgumentException("No member with that ID found, that is: " + memberId);
    }
    if (!m.getEmail().equals(newEmail) && !isUniqueEmail(newEmail)) {
      throw new IllegalArgumentException("Email already in use..");
    }
    if (!m.getPhone().equals(newPhoneNo) && !isUniquePhone(newPhoneNo)) {
      throw new IllegalArgumentException("Phone already in use..");
    }
    m.updateName(newName);
    m.updateEmail(newEmail);
    m.updatePhone(newPhoneNo);
    return m;
  }
}