package model.service;

/**
 * Service for managing time in the system.
 * Provides day counter functionality.
 */
public class TimeService {
  private int currentDay = 0;

  /**
   * Gets the current day.
   *
   * @return the current day starting from 0
   */
  public int getCurrentDay() {
    return currentDay;
  }

  /**
   * Increment the day counter by one.
   */
  public void advanceDay() {
    currentDay++;
  }
}
