package edu.ntnu.idatt2003.utils;

/**
 * Constants for resource paths used throughout the application.
 * <p>
 * Defines classpath locations for images, stylesheet, and overlay
 * configurations.
 * </p>
 */
public final class ResourcePaths {

  /** Directory for image resources on the classpath. */
  public static final String IMAGE_DIR = "/images/";

  /** JSON configuration file for 90-degree overlay. */
  public static final String OVERLAY_CONFIG_90 = "/overlays90.json";

  /** Stylesheet for JavaFX scenes. */
  public static final String STYLE_SHEET = "/styles/style.css";

  private ResourcePaths() {
    // Prevent instantiation
  }
}