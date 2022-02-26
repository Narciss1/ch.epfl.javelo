package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Q28_4Test {

    @Test
    void ofIntWorksOnKnownValues() {
        var actual1 = Q28_4.ofInt(160);
        var expected1 = 10;
        assertEquals(expected1, actual1);

        var actual2 = Q28_4.ofInt(304);
        var expected2 = 19;
        assertEquals(expected2, actual2);

        var actual3 = Q28_4.ofInt(272);
        var expected3 = 17;
        assertEquals(expected3, actual3);

        var actual4 = Q28_4.ofInt(32);
        var expected4 = 2;
        assertEquals(expected4, actual4);
    }

    @Test
    void asDoubleWorksOnKnownValues() {
        var actual1 = Q28_4.asDouble(160);
        var expected1 = 2560;
        assertEquals(expected1, actual1);

        var actual2 = Q28_4.asDouble(304);
        var expected2 = 4864;
        assertEquals(expected2, actual2);

        var actual3 = Q28_4.asDouble(272);
        var expected3 = 4352;
        assertEquals(expected3, actual3);

        var actual4 = Q28_4.asDouble(32);
        var expected4 = 512;
        assertEquals(expected4, actual4);
    }

    @Test
    void asFloatWorksOnKnownValues() {
        var actual1 = Q28_4.asFloat(160);
        var expected1 = 2560;
        assertEquals(expected1, actual1);

        var actual2 = Q28_4.asFloat(304);
        var expected2 = 4864;
        assertEquals(expected2, actual2);

        var actual3 = Q28_4.asFloat(272);
        var expected3 = 4352;
        assertEquals(expected3, actual3);

        var actual4 = Q28_4.asFloat(32);
        var expected4 = 512;
        assertEquals(expected4, actual4);
    }
}
