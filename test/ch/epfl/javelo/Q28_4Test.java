package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Q28_4Test {

    @Test
    void ofIntWorksOnKnownValues() {

        var actual1 = Q28_4.ofInt(1);
        var expected1 = 16;
        assertEquals(expected1, actual1);

        var actual2 = Q28_4.ofInt(19);
        var expected2 = 304;
        assertEquals(expected2, actual2);

        var actual3 = Q28_4.ofInt(17);
        var expected3 = 272;
        assertEquals(expected3, actual3);

        var actual4 = Q28_4.ofInt(2);
        var expected4 = 32;
        assertEquals(expected4, actual4);
    }

    @Test
    void asDoubleWorksOnKnownValues() {

        var actual1 = Q28_4.asDouble(1560);
        var expected1 = 97.5;
        assertEquals(expected1, actual1);

        var actual2 = Q28_4.asDouble(4864);
        var expected2 = 304;
        assertEquals(expected2, actual2);

        var actual3 = Q28_4.asDouble(4352);
        var expected3 = 272;
        assertEquals(expected3, actual3);

        var actual4 = Q28_4.asDouble(512);
        var expected4 = 32;
        assertEquals(expected4, actual4);
    }

    @Test
    void asFloatWorksOnKnownValues() {

        var actual1 = Q28_4.asFloat(2560);
        var expected1 = 160;
        assertEquals(expected1, actual1);

        var actual2 = Q28_4.asFloat(4864);
        var expected2 = 304;
        assertEquals(expected2, actual2);


        var actual3 = Q28_4.asFloat(4352);
        var expected3 = 272;
        assertEquals(expected3, actual3);

        var actual4 = Q28_4.asFloat(512);
        var expected4 = 32;
        assertEquals(expected4, actual4);
    }
}
