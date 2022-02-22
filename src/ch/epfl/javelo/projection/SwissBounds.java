package ch.epfl.javelo.projection;

public final class SwissBounds {

    private SwissBounds() {}

    final static double MIN_E = 2485000;
    final static double MAX_E = 2834000;
    final static double MIN_N = 1075000;
    final static double MAX_N = 1296000;
    final double WIDTH = MAX_E - MIN_E;
    final double HEIGHT = MAX_N - MIN_N;

    public static boolean containsEN(double e, double n){
        if((e <= MAX_E)&&(e >= MIN_N)&&(n <= MAX_N)&&(n >= MIN_N)){ return true; }
        return false;
    }
}
