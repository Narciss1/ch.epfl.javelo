package ch.epfl.javelo;

import static java.lang.Math.scalb;

/**
 * Contains static methods to convert numbers between the Q28.4
 * representation and other representations
 * @author Aya Hamane (345565)
 */
public final class Q28_4 {

    /**
     * Minimum value of zoom level
     */
    private final static int NUMBER_BITS_SHIFT = 4;

    private Q28_4(){}

    /**
     * Calculates the Q28.4 value corresponding to a given integer
     * @param i integer
     * @return corresponding Q28.4 value
     */
    public static int ofInt(int i) { return i << NUMBER_BITS_SHIFT; }

    /**
     * Calculates  the value of type double corresponding to a given Q28.4 value
     * @param q28_4 Q28.4 value
     * @return corresponding value of type double
     */
    public static double asDouble(int q28_4) { return scalb((double) q28_4, -NUMBER_BITS_SHIFT); }

    /**
     * Calculates  the value of type float corresponding to a given Q28.4 value
     * @param q28_4 Q28.4 value
     * @return corresponding value of type float
     */
    public static float asFloat(int q28_4) { return scalb(q28_4, -NUMBER_BITS_SHIFT); }
}
