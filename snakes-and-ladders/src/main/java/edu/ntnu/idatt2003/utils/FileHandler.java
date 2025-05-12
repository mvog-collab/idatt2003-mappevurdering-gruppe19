package edu.ntnu.idatt2003.utils;

import java.io.IOException;
import java.nio.file.Path;

public interface FileHandler<T> {

  void save(T object, Path path) throws IOException;

  T load(Path path) throws IOException;
}
