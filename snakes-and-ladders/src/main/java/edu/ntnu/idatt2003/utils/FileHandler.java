package edu.ntnu.idatt2003.utils;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Generic interface for handling file persistence operations.
 *
 * @param <T> the type of object to save and load
 */
public interface FileHandler<T> {

  /**
   * Saves the given object to the specified path.
   *
   * @param object the object to save
   * @param path   the file path where the object should be written
   * @throws IOException if an I/O error occurs during saving
   */
  void save(T object, Path path) throws IOException;

  /**
   * Loads an object from the specified path.
   *
   * @param path the file path from which to read the object
   * @return the loaded object
   * @throws IOException if an I/O error occurs during loading
   */
  T load(Path path) throws IOException;
}