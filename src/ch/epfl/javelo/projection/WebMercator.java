package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * Provides static methods to convert between WGS 84 and Web Mercator coordinates
 * @author Aya Hamane (345565)
 */
public final class WebMercator {

    private WebMercator(){}

    /**
     * Calculates the coordinate x of the projection of a point at longitude lon, given in radians
     * @param lon longitude of a point
     * @return coordinate x of the point
     */
    public static double x(double lon) {
        return (1.0 / (2.0*Math.PI)) * (lon + Math.PI);
    }

    /**
     * Calculates the coordinate y of the projection of a point at latitude lat, given in radians
     * @param lat latitude of a point
     * @return coordinate y of the point
     */
    public static double y(double lat){
        return (1.0 / (2.0*Math.PI)) * (Math.PI - Math2.asinh(Math.tan(lat)));
    }

    /**
     * Calculates the longitude of a point whose projection is at the given x coordinate
     * @param x coordinate of the point
     * @return longitude in radians
     */
    public static double lon(double x){
        return 2.0 * Math.PI * x - Math.PI;
    }

    /**
     * Calculates the latitude of a point whose projection is at the given y coordinate
     * @param y coordinate of the point
     * @return latitude in radians
     */
    public static double lat(double y){
        return Math.atan(Math.sinh(Math.PI - 2.0 * Math.PI * y));
    }
}
