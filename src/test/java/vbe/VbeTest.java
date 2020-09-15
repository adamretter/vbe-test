package vbe;

import org.junit.jupiter.api.RepeatedTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VbeTest {

    @RepeatedTest(10)
    public void simplifiedTest() throws IOException {
        final VbeMain.IntTuple result = VbeMain.simplifiedTest();
        assertEquals(result.expected, result.actual);
    }
}
