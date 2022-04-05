package ch.epfl.javelo.projection;

import static ch.epfl.javelo.Preconditions.checkArgument;
import static ch.epfl.javelo.projection.SwissBounds.containsEN;

public record PointCh(double e, double n) {

    /**
     * Compact constructor
     * @param e east coordinate of the point
     * @param n north coordinate of the point
     */
    public PointCh {
        checkArgument(containsEN(e, n));
    }

    /**
     * Calculates the squared distance in meters separating the receiver and the argument
     * @param that point in the map
     * @return squared distance between two points
     */
    public double squaredDistanceTo(PointCh that){
        return Math.pow(this.e - that.e, 2) + Math.pow(this.n - that.n, 2);
    }

    /**
     * Calculates the distance in meters separating the receiver and the argument
     * @param that point in the map
     * @return distance between two points
     */
    public double distanceTo(PointCh that){
        return Math.sqrt(Math.pow(this.e - that.e, 2) + Math.pow(this.n - that.n, 2));
    }

    /**
     * @return longitude in radians of a point in the WGS84 system
     */
    public double lon() { return Ch1903.lon(e, n); }

    /**
     * @return latitude in radians of a point in the WGS84 system
     */
    public double lat(){ return Ch1903.lat(e, n); }

    /**
     * @return east coordinate of a point
     */
    public double getE(){
        return e;
    }

    /**
     * @return the north coordinate of a point
     */
    public double getN(){
        return n;
    }
}