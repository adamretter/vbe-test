# Variable Byte Encoding - Test

Results appear to be non-deterministic on JDK 14, and 13, but fine on earlier JDKs.

If you invoke `VbeTest` repetitively then you will eventually see it fail on JDK 14 or 13, but not earlier JDKs.
By default `VbeTest` is configured to run for 10 iterations with JUnit 5 - this can be invoked by running: `mvn clean test`.

