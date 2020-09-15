# Variable Byte Encoding - Test

Results appear to be non-deterministic on JDK 12, 13, and 14, but fine on earlier JDKs.

If you invoke `VbeMain#simplifiedTest()` repetitively then you will eventually see it fail on JDKs 12 - 14, but pass fine on earlier JDKs.

You can either run it directly via `VbeMain`:
```bash
mvn clean compile
cd target/classes
java vbe.VbeMain
```
By default `VbeMain` is configured to run for 10 iterations.

Otherwise, you can run it via `VbeTest` using Surefire and JUnit:
```bash
mvn clean test
```
By default `VbeTest` is configured to run for 10 iterations with JUnit 5.

## Diagnosis

In progress...

* If you comment out the *skip* part, the test always passes...
```java
final boolean skip = false;  // temporarily disabled!
// final boolean skip = tmp % 7 == 0;
if (skip) {
```

* [Ben Evans](https://github.com/kittylyst) kindly diagnosed that disabling JIT
by passing `-Xint` to Java (HotSpot) causes the tests to pass.
    
    If using Surefire and JUnit, we can specify `mvn test -DargLine=-Xint`.
    
* [mbien](https://github.com/) points out that `-Xint` may be equivalent to `-XX:TieredStopAtLevel=0`,
and that by trying `-XX:TieredStopAtLevel=3` we can rule out C1.

    If using Surefire and JUnit, we can specify `mvn test -DargLine="-XX:TieredStopAtLevel=3"`.

* We have likely discovered a regress in HotSpot C2 on Java 12+, so here is the bug report: [JDK-8253191](https://bugs.openjdk.java.net/browse/JDK-8253191).

## Testing Results

Tested on macOS Catalina (10.15.6) with Intel Core i9. Similar results have also been observed on Ubuntu Xenial Linux x64.

| AdoptOpenJDK  | HotSpot/J9 | -Xint | Passes |
|---------------|------------|-------|--------|
| 1.8.0_265-b01 | HotSpot    | No    | Yes    |
| 1.8.0_265-b01 | HotSpot    | Yes   | Yes    |
| 1.8.0_265-b01 | J9         | No    | Yes    |
| 1.8.0_265-b01 | J9         | Yes   | Yes    |
| 9+181         | HotSpot    | No    | Yes    |
| 10.0.2+13     | HotSpot    | No    | Yes    |
| 10.0.2+13     | HotSpot    | Yes   | Yes    |
| 11.0.8+10     | HotSpot    | No    | Yes    |
| 11.0.8+10     | HotSpot    | Yes   | Yes    |
| 11.0.8+10     | J9         | No    | Yes    |
| 11.0.8+10     | J9         | Yes   | Yes    |
| 12.0.2+10     | HotSpot    | No    | **FAIL**   |
| 12.0.2+10     | HotSpot    | Yes   | Yes    |
| 12.0.2+10     | J9         | No    | Yes    |
| 12.0.2+10     | J9         | Yes   | Yes    |
| 13.0.2+8      | HotSpot    | No    | **FAIL**   |
| 13.0.2+8      | HotSpot    | Yes   | Yes    |
| 13.0.2+8      | J9         | No    | Yes    |
| 13.0.2+8      | J9         | Yes   | Yes    |
| 14.0.2+12     | HotSpot    | No    | **FAIL**   |
| 14.0.2+12     | HotSpot    | Yes   | Yes    |
| 14.0.2+12     | J9         | No    | Yes    |
| 14.0.2+12     | J9         | Yes   | Yes    |
| (jdk.java.net) 15+36-1562    | HotSpot    | No    | **FAIL**   |
| (jdk.java.net) 15+36-1562    | HotSpot    | Yes   | Yes    |

The tests that fail are all HotSpot based JDK 12 - 15. Passing the argument `-Xint` or `-XX:TieredStopAtLevel=3` causes
the tests those tests to pass, which suggests the issue may be in the C2 compiler. 

## Travis CI results

Latest results [here](https://travis-ci.com/github/adamretter/vbe-test).
