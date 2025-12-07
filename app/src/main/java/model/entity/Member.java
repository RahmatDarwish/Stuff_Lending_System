package model.entity;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * Represents a member in the stuff lending system.
 */

public class Member {

  private final String memberId;
  private final int createdDate;
  private String name;
  private String email;
  private String phone;
  private double credit = 0;

  private List<Item> ownedItems;


  /**
   * Class constructor.
   *
   * @param memberId  the unique member ID
   * @param name      the member's name
   * @param email     the member's email
   * @param phone     the member's phone
   * @param createDay the day the member was created
   */

  public Member(String memberId, String name, String email, String phone, int createDay) {

    this.memberId = Objects.requireNonNull(memberId, "member ID required");

    this.name = Objects.requireNonNull(name, "name required");

    this.phone = Objects.requireNonNull(phone, "phone no required");

    this.email = Objects.requireNonNull(email, "email required");

    if (!email.contains("@")) {

      throw new IllegalArgumentException("Invalid email needs to contain '@'");

    }

    this.createdDate = createDay;

    this.ownedItems = new ArrayList<>();

  }


  /**
   * Constructor for testing purposes - generates a random test ID.
   *
   * @param name      the member's name
   * @param email     the member's email
   * @param phone     the member's phone
   * @param createDay the day the member was created
   * @deprecated Use the constructor with explicit ID for production code
   */

  @Deprecated

  public Member(String name, String email, String phone, int createDay) {

    this(String.format("T%05d", System.nanoTime() % 100000), name, email, phone, createDay);

  }


  /**
   * Copy constructor to hide the implementation details of the member class.
   *
   * @param x is the member to be copied.
   */

  public Member(Member x) {

    this.memberId = x.memberId;

    this.name = x.name;

    this.phone = x.phone;

    this.email = x.email;

    this.createdDate = x.createdDate;

    this.credit = x.credit;

    // for deep copy sake

    this.ownedItems = new ArrayList<>(x.ownedItems);

  }


  /**
   * Gets the member ID.
   *
   * @return the member ID
   */

  public String getMemberId() {

    return memberId;

  }


  /**
   * Gets the member name.
   *
   * @return the member name
   */

  public String getName() {

    return name;

  }


  /**
   * Gets the member email.
   *
   * @return the member email
   */

  public String getEmail() {

    return email;

  }


  /**
   * Gets the member phone.
   *
   * @return the member phone
   */

  public String getPhone() {

    return phone;

  }


  /**
   * Gets the member credit.
   *
   * @return the member credit
   */

  public double getCredit() {

    return credit;

  }


  /**
   * Sets the member credit.
   *
   * @param credit the new credit amount
   */

  public void setCredit(double credit) {

    if (credit < 0) {

      throw new IllegalArgumentException("Credits cannot be negative");

    }

    this.credit = credit;

  }


  /**
   * Gets the member creation date.
   *
   * @return the creation date
   */

  public Integer getCreatedDate() {

    return createdDate;

  }


  /**
   * Deducts credit from the member.
   *
   * @param amount the amount to deduct
   */

  public void deductCredit(double amount) {

    if (amount <= 0) {

      throw new IllegalArgumentException("Amount cannot be less than 0");

    }

    if (this.credit < amount) {

      throw new IllegalArgumentException("Insufficient credit");

    }

    this.credit -= amount;

  }


  /**
   * Updates the member's email.
   *
   * @param newEmail the new email
   */

  public void updateEmail(String newEmail) {

    this.email = Objects.requireNonNull(newEmail).trim();

  }


  /**
   * Updates the member's phone.
   *
   * @param newPhoneNo the new phone number
   */

  public void updatePhone(String newPhoneNo) {

    this.phone = Objects.requireNonNull(newPhoneNo).trim();

  }


  /**
   * Updates the member's name.
   *
   * @param newName the new name
   */

  public void updateName(String newName) {

    this.name = Objects.requireNonNull(newName).trim();

  }


  /**
   * Increases the member's credit.
   *
   * @param amount the amount to increase
   */

  public void increaseCredit(double amount) {

    if (amount <= 0) {

      throw new IllegalArgumentException("Amount cannot be less than 0");

    }

    this.credit += amount;

  }


  /**
   * Method to add an item to the ownedItems list.
   *
   * <p>Awards +100 credits when registering a new item.
   *
   * @param item The item to add to the members owned items.
   */

  public void addItem(Item item) {

    ownedItems.add(item);

    this.credit += 100;

  }


  /**
   * Gets the member's owned items.
   *
   * @return an unmodifiable list of owned items
   */

  public List<Item> getOwnedItems() {

    return Collections.unmodifiableList(ownedItems);

  }


  /**
   * Removes an item from the member's owned items.
   *
   * @param item the item to remove
   */

  public void removeItem(Item item) {

    if (!ownedItems.remove(item)) {

      throw new IllegalArgumentException("No such item found");

    }

  }


  @Override

  public String toString() {

    return String.format("Member[id=%s, name=%s, email=%s, credit=%.2f, created=%d, items=%d]",

        memberId, name, email, credit, createdDate, ownedItems.size());

  }

}