package com.github.brymck.withenvironment;

/** */
@FunctionalInterface
public interface WithEnvironmentBlock {
  void run();

  default WithEnvironmentBlock andThen(WithEnvironmentBlock after) {
    return () -> {
      this.run();
      after.run();
    };
  }

  default WithEnvironmentBlock compose(WithEnvironmentBlock before) {
    return () -> {
      before.run();
      this.run();
    };
  }
}
