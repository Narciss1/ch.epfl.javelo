package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BitsTest {

    public static final double DELTA = 1e-11;

    @Test
    void bitsExtractSignedThrowsOnInvalidRange() {

        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(2038,-1,28);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(20,6,33);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(203881,0,40);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(2,-1,2);
        });
    }

    @Test
    void bitsExtractSignedWorksOnKnownValues() {

        var actual1 = Bits.extractSigned(20,6,8);
        var expected1 = 0;
        assertEquals(expected1, actual1);

        var actual2 = Bits.extractSigned(27452,3,10);
        var expected2 = 359;
        assertEquals(expected2, actual2);

        var actual3 = Bits.extractSigned(38563,0,5);
        var expected3 = 3;
        assertEquals(expected3, actual3);

        var actual4 = Bits.extractSigned(19270,4,11);
        var expected4 = 0b11111111111111111111110010110100;
        assertEquals(expected4, actual4);
    }

   @Test
    void bitsExtractUnsignedThrowsOnInvalidRange() {

        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(2038,-1,28);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(19,5,32);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(2073532,0,40);
        });assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(2,-2,2);
        });
    }

    @Test
    void bitsExtractUnsignedWorksOnKnownValues() {

        var actual1 = Bits.extractUnsigned(291820,6,8);
        var expected1 = 207;
        assertEquals(expected1, actual1);

        var actual2 = Bits.extractUnsigned(27822,3,10);
        var expected2 = 405;
        assertEquals(expected2, actual2);

        var actual3 = Bits.extractUnsigned(385392,0,5);
        var expected3 = 16;
        assertEquals(expected3, actual3);

        var actual4 = Bits.extractUnsigned(19000,4,11);
        var expected4 = 1187;
        assertEquals(expected4, actual4);
    }

    @Test
    void extractSigned() {
        assertEquals(329, Bits.extractSigned(0b10010101_01001010_01001010_01001010, 3, 10));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractSigned(1, -1, 10));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractSigned(1, 1000, 10));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractSigned(1, 33, 10));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractSigned(1, 4, -1));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractSigned(1, 4, 33));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractSigned(1, 4, 30));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractSigned(1, 15, 18));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractSigned(1, 0, 35));
        assertEquals(-183, Bits.extractSigned(0b10010101_01001010_01001010_01001010, 3, 9));
        assertEquals(-1, Bits.extractSigned(0b10010101_01001010_01001010_01001010, 3, 1));
        assertEquals(4754, Bits.extractSigned(0b10010101_01001010_01001010_01001010, 10, 14));
        assertEquals(0b10010101_01001010_01001010_01001010, Bits.extractSigned(0b10010101_01001010_01001010_01001010, 0, 32));
        assertEquals(0, Bits.extractSigned(0b10010101_01001010_01001010_01001010, 0, 1));
    }

    @Test
    void extractUnsigned() {
        assertEquals(329, Bits.extractUnsigned(0b10010101_01001010_01001010_01001010, 3, 10));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractUnsigned(1, -1, 10));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractUnsigned(1, 1000, 10));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractUnsigned(1, 33, 10));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractUnsigned(1, 4, -1));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractUnsigned(1, 4, 33));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractUnsigned(1, 4, 30));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractUnsigned(1, 0, 32));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractUnsigned(1, 15, 18));
        assertThrows(IllegalArgumentException.class, () -> Bits.extractUnsigned(1, 0, 35));
        assertEquals(329, Bits.extractUnsigned(0b10010101_01001010_01001010_01001010, 3, 9));
        assertEquals(1, Bits.extractUnsigned(0b10010101_01001010_01001010_01001010, 3, 1));
        assertEquals(4754, Bits.extractUnsigned(0b10010101_01001010_01001010_01001010, 10, 14));
        assertEquals(0b0010101_01001010_01001010_01001010, Bits.extractUnsigned(0b10010101_01001010_01001010_01001010, 0, 31));
        assertEquals(0, Bits.extractUnsigned(0b10010101_01001010_01001010_01001010, 0, 1));
    }

    @Test
    void extractSignedToThrowOnInvalidValues(){
        assertThrows(IllegalArgumentException.class, () ->{
            int actual1 = Bits.extractSigned(5, -1, 4);
        });
        assertThrows(IllegalArgumentException.class, () ->{
            int actual2 = Bits.extractSigned(5, 1, 33);
        });
        assertThrows(IllegalArgumentException.class, () ->{
            int actual3 = Bits.extractSigned(5, 33, 33);
        });
        assertThrows(IllegalArgumentException.class, () ->{
            int actual4 = Bits.extractSigned(5, 1, 0);
        });
    }

    @Test
    void extractUnsignedToThrowOnInvalidValues(){
        assertThrows(IllegalArgumentException.class, () ->{
            int actual1 = Bits.extractUnsigned(5, -1, 4);
        });
        assertThrows(IllegalArgumentException.class, () ->{
            int actual2 = Bits.extractUnsigned(5, 1, 33);
        });
        assertThrows(IllegalArgumentException.class, () ->{
            int actual3 = Bits.extractUnsigned(5, 33, 33);
        });
        assertThrows(IllegalArgumentException.class, () ->{
            int actual4 = Bits.extractUnsigned(5, 1, -1);
        });
    }

    @Test
    void extractSignedToWorkOnValidValues(){
        assertEquals(0b11111111111111111111111111111010, Bits.extractSigned(0b11001010111111101011101010111110, 8, 4));
    }

    @Test
    void extractUnsignedToWorkOnValidValues(){
        assertEquals(0b00000000000000000000000000001010, Bits.extractUnsigned(0b11001010111111101011101010111110, 8, 4));
    }
}
