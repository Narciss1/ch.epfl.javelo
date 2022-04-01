package ch.epfl.javelo.projection;

public final class SwissBounds {

    private SwissBounds() {}

    /**
     * smallest east coordinate of Switzerland
     */
    public final static double MIN_E = 2485000;
    /**
     * biggest east coordinate of Switzerland
     */
    public final static double MAX_E = 2834000;
    /**
     * smallest north coordinate of Switzerland
     */
    public final static double MIN_N = 1075000;
    /**
     * biggest north coordinate of Switzerland
     */
    public final static double MAX_N = 1296000;
    /**
     * width of Switzerland in meters
     */
    public final static double WIDTH = MAX_E - MIN_E;
    /**
     * height of Switzerland in meters
     */
    public final static double HEIGHT = MAX_N - MIN_N;

    /**
     * Verifies if a point is in the limits of Switzerland
     * @param e east coordinate
     * @param n north coordinate
     * @return true if both coordinates of the point are in the limits of Switzerland
     */
    public static boolean containsEN(double e, double n){
        return !(e < MIN_E || e > MAX_E || n < MIN_N || n > MAX_N);
    }

}
