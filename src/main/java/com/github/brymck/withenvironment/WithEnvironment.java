package com.github.brymck.withenvironment;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class allows executing a code block with a modified system environment.
 *
 * <p>For example:
 *
 * <pre>{@code
 * Map<String, String> overrides = new HashMap();
 * overrides.put("FOO", "bar");
 * WithEnvironment.withEnvironmentOverrides(overrides, () -> {
 *   String foo = System.getenv("FOO");
 *   System.out.println(foo);  // prints "bar"
 * });
 * }</pre>
 *
 * @author Bryan McKelvey
 */
public class WithEnvironment {
  private static Logger logger = LoggerFactory.getLogger(WithEnvironment.class);

  /**
   * Run a block within an environment with variables overridden based on the provided {@link Map}.
   *
   * @param overrides a {@link Map} of overridden keys and values to apply to the environment
   * @param block a lambda running within the overridden environment
   */
  public static void withEnvironmentOverrides(
      @NotNull Map<@NotNull String, @Nullable String> overrides,
      @NotNull WithEnvironmentBlock block) {
    Map<String, String> originalVariables = new HashMap<>(System.getenv());
    Map<String, String> environment = getEditableMapOfVariables();
    Map<String, String> caseInsensitiveEnvironment = getCaseInsensitiveEnvironment();
    logger.debug("Overriding environment...");
    try {
      overrides.forEach(
          (key, value) -> {
            logger.debug("Setting environment variable " + key + " to " + value);
            if (!originalVariables.containsKey(key)) {
              originalVariables.put(key, null);
            }
            setEnvironment(environment, key, value);
            setEnvironment(caseInsensitiveEnvironment, key, value);
          });
      logger.debug("Running block passed to withEnvironmentOverrides...");
      block.run();
    } finally {
      logger.debug("Resetting environment...");
      originalVariables.forEach(
          (key, value) -> {
            logger.debug("Setting environment variable " + key + " to " + value);
            setEnvironment(environment, key, value);
            setEnvironment(caseInsensitiveEnvironment, key, value);
          });
    }
  }

  /**
   * Sets the value for a single environment variable.
   *
   * @param environment a map of all environment variable
   * @param key the environment variable's name
   * @param value the value to assign the environment variable
   */
  private static void setEnvironment(
      @Nullable Map<@NotNull String, @NotNull String> environment,
      @NotNull String key,
      @Nullable String value) {
    if (environment == null) {
      return;
    }
    if (value == null) {
      environment.remove(key);
    } else {
      environment.put(key, value);
    }
  }

  /**
   * Retrieves en editable map of all environment variables.
   *
   * @return a mutable {@link Map} of environment variables
   */
  private static @NotNull Map<@NotNull String, @NotNull String> getEditableMapOfVariables() {
    Class classOfMap = System.getenv().getClass();
    Map<String, String> map;

    try {
      map = getFieldValue(classOfMap, System.getenv(), "m");
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Cannot access the field 'm' of the map System.getenv().", e);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(
          "Expecting System.getenv() to have a field 'm' but it does not.", e);
    }

    if (map == null) {
      throw new RuntimeException(
          "Expecting System.getenv() to have a map in field 'm' but it is null.");
    } else {
      return map;
    }
  }

  /**
   * Retrieves a case-insensitive map of environment variables.
   *
   * @return a mutable {@link Map} of environment variables
   */
  private static @Nullable Map<@NotNull String, @NotNull String> getCaseInsensitiveEnvironment() {
    try {
      Class processEnvironment = Class.forName("java.lang.ProcessEnvironment");
      return getFieldValue(processEnvironment, null, "theCaseInsensitiveEnvironment");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(
          "Expecting the existence of the class java.lang.ProcessEnvironment but it does not exist.",
          e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(
          "Cannot access the static field 'theCaseInsensitiveEnvironment' of the class java.lang.ProcessEnvironment.",
          e);
    } catch (NoSuchFieldException e) {
      // This field is only available for Windows
      return null;
    }
  }

  /**
   * Gets the value of a keyed field on a class.
   *
   * @param klass the class
   * @param key the class field's key
   * @param name the name of the value in the field
   * @return a {@link Map} of the field
   * @throws NoSuchFieldException when the field cannot be found
   * @throws IllegalAccessException when the underlying field is inaccessible
   */
  @SuppressWarnings("unchecked")
  private static Map<String, String> getFieldValue(Class klass, Object key, String name)
      throws NoSuchFieldException, IllegalAccessException {
    Field field = klass.getDeclaredField(name);
    field.setAccessible(true);
    return (Map<String, String>) field.get(key);
  }
}
