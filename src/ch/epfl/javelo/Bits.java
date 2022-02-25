package ch.epfl.javelo;

public final class Bits {

    private Bits(){}

    /**
     * Extracts from the 32-bit vector value the range of length bits starting at
     * the index bit start, which it interprets as a two's complement signed value or
     * throws an exception if the range is invalid
     * @param value a 32-bit vector
     * @param start the index representing the start of the range of bits to extract
     * @param length of the range of bits to extract
     * @return the extracted range of bits
     */
    //USE CONSTANTE JAVA POUR 32
    //add variable 32 - length
    //Preconditions à add
    public int extractSigned(int value, int start, int length){
        if(start>=0 && start<=31 && length>=0 && length<=32){
            int newValue = (value << (32-start-length)) >> (32-length) ;
            return newValue;
        }else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * Extracts from the 32-bit vector value the range of length bits starting at
     * the index bit start, which it interprets as a two's complement unsigned value or
     * throws an exception if the range is invalid
     * @param value a 32-bit vector
     * @param start the index representing the start of the range of bits to extract
     * @param length of the range of bits to extract
     * @return the extracted range of bits
     */
    public int extractUnsigned(int value, int start, int length){
        if(start>= 0 && start<=31 && length>=0 && length<32){
            int newValue = (value << (32-start-length)) >>> (32-length);
            return newValue;
        }else{
            throw new IllegalArgumentException();
        }
    }
}
