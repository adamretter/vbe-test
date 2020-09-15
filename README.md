# Variable Byte Encoding - Test

Results appear to be non-deterministic on JDK 14, and 13, but fine on earlier JDKs.

If you invoke `VbeTest` repetitively then you will eventually see it fail on JDK 14 or 13, but not earlier JDKs.
By default `VbeTest` is configured to run for 10 iterations with JUnit 5 - this can be invoked by running: `mvn clean test`.

## Diagnosis

In progress...

* If you comment out the *skip* part, the test always passes...
```java
final boolean skip = false;  // temporarily disabled!
// final boolean skip = tmp % 7 == 0;
if (skip) {
```

## Travis CI results

Latest results [here](https://travis-ci.com/github/adamretter/vbe-test).
