package model;

/**
 * Interface for data persistence operations.
 * Provides methods for loading and saving data.
 */
public interface PersistenceInterface {
  /**
   * Load data from the persistence layer.
   */
  void loadData();

  /**
   * Save data to the persistence layer.
   */
  void saveData();
}
