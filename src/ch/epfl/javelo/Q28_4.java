package ch.epfl.javelo;

import static java.lang.Math.scalb;

public final class Q28_4 {

    private Q28_4(){}

    /**
     * Calculates the Q28.4 value corresponding to a given integer
     * @param i integer
     * @return corresponding Q28.4 value
     */
    public static int ofInt(int i) { return i << 4; }

    /**
     * Calculates  the value of type double corresponding to a given Q28.4 value
     * @param q28_4 Q28.4 value
     * @return corresponding value of type double
     */
    public static double asDouble(int q28_4) { return scalb((double) q28_4, -4); }

    /**
     * Calculates  the value of type float corresponding to a given Q28.4 value
     * @param q28_4 Q28.4 value
     * @return corresponding value of type float
     */
    public static float asFloat(int q28_4) { return scalb(q28_4, -4); }
}
