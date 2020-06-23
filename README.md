with-environment
================

[![CircleCI](https://circleci.com/gh/brymck/with-environment.svg?style=shield)](https://circleci.com/gh/brymck/with-environment)
[![codecov](https://codecov.io/gh/brymck/with-environment/branch/master/graph/badge.svg)](https://codecov.io/gh/brymck/with-environment)

`with-environment` is a library that allows overriding the system environment and executing a block.

Much of this is shamelessly yanked from [SystemRules][system-rules] and is just designed to decouple
the functionality from JUnit 4 and provide a natural Kotlin interface.

Usage
-----

Include this in your POM:

```xml
<dependency>
  <groupId>com.github.brymck</groupId>
  <artifactId>with-environment</artifactId>
  <version>0.9.1</version>
</dependency>
```

And use it as s o in Java:

```java
Map<String, String> overrides = new HashMap<>();
overrides.put("FOO", "bar");
WithEnvironment.withEnvironmentOverrides(overrides, () -> {
  String foo = System.getenv("FOO");
  System.out.println(foo);  // prints "bar"
});
```

or Kotlin:

```kotlin
val overrides = mapOf("FOO" to "bar")
withEnvironmentOverrides(overrides) {
  val foo = System.getenv("FOO");
  println(foo)  // prints "bar"
}
```

[system-rules]: https://github.com/stefanbirkner/system-rules
