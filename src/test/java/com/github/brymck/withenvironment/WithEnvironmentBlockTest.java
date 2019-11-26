package com.github.brymck.withenvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("withEnvironment block")
public class WithEnvironmentBlockTest {
  @Test
  @DisplayName("is runnable")
  void isRunnable() {
    AtomicInteger i = new AtomicInteger(0);

    WithEnvironmentBlock incrementI = i::getAndIncrement;

    assertEquals(0, i.intValue());
    incrementI.run();
    assertEquals(1, i.intValue());
  }

  @Test
  @DisplayName("can be chained with andThen")
  void canBeChainedWithAndThen() {
    AtomicInteger i = new AtomicInteger(0);

    WithEnvironmentBlock incrementI = i::getAndIncrement;
    WithEnvironmentBlock multiplyByTwo = () -> i.set(i.get() * 2);

    assertEquals(0, i.intValue());
    incrementI.andThen(multiplyByTwo).run();
    assertEquals(2, i.intValue());
  }

  @Test
  @DisplayName("can be chained with compose")
  void canBeChainedWithCompose() {
    AtomicInteger i = new AtomicInteger(0);

    WithEnvironmentBlock incrementI = i::getAndIncrement;
    WithEnvironmentBlock multiplyByTwo = () -> i.set(i.get() * 2);

    assertEquals(0, i.intValue());
    multiplyByTwo.compose(incrementI).run();
    assertEquals(2, i.intValue());
  }
}
