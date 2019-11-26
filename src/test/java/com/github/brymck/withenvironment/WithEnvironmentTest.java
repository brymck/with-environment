package com.github.brymck.withenvironment;

import static com.github.brymck.withenvironment.WithEnvironment.withEnvironmentOverrides;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import org.junit.jupiter.api.*;

@DisplayName("withEnvironment")
class WithEnvironmentTest {
  @Test
  @DisplayName("pre-test environment has no FOO environment variable set")
  void preTestEnvironmentHasNoFooEnvironmentVariableSet() {
    assertNull(System.getenv("FOO"));
  }

  @Test
  @DisplayName("overrides environment variables")
  void overridesEnvironmentVariables() {
    HashMap<String, String> overrides = new HashMap<>();
    overrides.put("FOO", "bar");
    overrides.put("HOGE", "poge");
    assertNull(System.getenv("FOO"));
    assertNull(System.getenv("HOGE"));
    withEnvironmentOverrides(
        overrides,
        () -> {
          assertEquals("bar", System.getenv("FOO"));
          assertEquals("poge", System.getenv("HOGE"));
        });
  }

  @Test
  @DisplayName("resets environment variables after running block")
  void resetsEnvironmentVariablesAfterRunningBlock() {
    HashMap<String, String> overrides = new HashMap<>();
    overrides.put("FOO", "bar");
    overrides.put("HOGE", "poge");
    assertNull(System.getenv("FOO"));
    assertNull(System.getenv("HOGE"));
    withEnvironmentOverrides(
        overrides,
        () -> {
          assertEquals("bar", System.getenv("FOO"));
          assertEquals("poge", System.getenv("HOGE"));
        });
    assertNull(System.getenv("FOO"));
    assertNull(System.getenv("HOGE"));
  }

  @Test
  @DisplayName("resets environment variables after exception is thrown")
  void resetsEnvironmentVariablesAfterExceptionIsThrown() {
    HashMap<String, String> overrides = new HashMap<>();
    overrides.put("FOO", "bar");
    overrides.put("HOGE", "poge");
    assertNull(System.getenv("FOO"));
    assertNull(System.getenv("HOGE"));
    assertThrows(
        RuntimeException.class,
        () -> {
          withEnvironmentOverrides(
              overrides,
              () -> {
                throw new RuntimeException(":(");
              });
        });
    assertNull(System.getenv("FOO"));
    assertNull(System.getenv("HOGE"));
  }

  @Test
  @DisplayName("allows layers of overrides")
  void allowsLayersOfOverrides() {
    HashMap<String, String> fooOverride = new HashMap<>();
    fooOverride.put("FOO", "bar");
    HashMap<String, String> hogeOverride = new HashMap<>();
    hogeOverride.put("HOGE", "poge");
    assertNull(System.getenv("FOO"));
    assertNull(System.getenv("HOGE"));
    withEnvironmentOverrides(
        fooOverride,
        () -> {
          assertEquals("bar", System.getenv("FOO"));
          assertNull(System.getenv("HOGE"));
          withEnvironmentOverrides(
              hogeOverride,
              () -> {
                assertEquals("bar", System.getenv("FOO"));
                assertEquals("poge", System.getenv("HOGE"));
              });
          assertEquals("bar", System.getenv("FOO"));
          assertNull(System.getenv("HOGE"));
        });
    assertNull(System.getenv("FOO"));
    assertNull(System.getenv("HOGE"));
  }
}
