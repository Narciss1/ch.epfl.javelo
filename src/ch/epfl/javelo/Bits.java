package ch.epfl.javelo;

import static ch.epfl.javelo.Preconditions.checkArgument;

public final class Bits {

    private Bits(){}

    /**
     * Extracts from the 32-bit vector value the range of length bits starting at
     * the index bit start, which it interprets as a two's complement signed value or
     * throws an exception if the range is invalid
     * @param value 32-bit vector
     * @param start index representing the start of the range of bits to extract
     * @param length length of the range of bits to extract
     * @return extracted range of bits
     * @throws IllegalArgumentException if the range is invalid
     */
    public static int extractSigned(int value, int start, int length){
        int x = Integer.SIZE - length;
        checkArgument(start >= 0 && start <= 31 && length >= 1 && 0 <= x && x-start >= 0);
        return (value << (x - start)) >> (x);
    }

    /**
     * Extracts from the 32-bit vector value the range of length bits starting at
     * the index bit start, which it interprets as a two's complement unsigned value or
     * throws an exception if the range is invalid
     * @param value 32-bit vector
     * @param start index representing the start of the range of bits to extract
     * @param length length of the range of bits to extract
     * @return the extracted range of bits
     * @throws IllegalArgumentException if the range is invalid
     */
    public static int extractUnsigned(int value, int start, int length){
        int x = Integer.SIZE - length;
        checkArgument(start >= 0 && start <= 31 && length >= 1 && 0 < x && x-start >= 0);
        return (value << (x - start)) >>> (x);
    }
}
