package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Q28_4TestRayan {

    @Test
    void Q28_4WorksOnKnownValues() {
        var value = 0b11001010111111101011101010111110;
        var expectedBits = 0b10101111111010111010101111100000;
        var expectedValue = Math.scalb(expectedBits, -4);
        var actual = Q28_4.asFloat(Q28_4.ofInt(value));
        assertEquals(expectedValue, actual);
    }

}