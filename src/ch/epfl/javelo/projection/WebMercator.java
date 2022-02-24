package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

public final class WebMercator {

    private WebMercator(){}

    /**
     * Calculates the coordinate x of the projection of a point at longitude lon, given in radians
     * @param lon the longitude of a point
     * @return the coordinate x of the point
     */
    public static double x(double lon){
        return (1/(2*Math.PI))*(lon + Math.PI);
    }

    /**
     * Calculates the coordinate y of the projection of a point at latitude lat, given in radians
     * @param lat the latitude of a point
     * @return the coordinate y of the point
     */
    public static double y(double lat){
        return (1/(2*Math.PI))*(Math.PI - Math2.asinh(Math.tan(lat)));
    }

    /**
     * Calculates the longitude of a point whose projection is at the given x coordinate
     * @param x a coordinate of the point
     * @return the longitude in radians
     */
    public static double lon(double x){
        return 2*Math.PI*x - Math.PI;
    }

    /**
     * Calculates the latitude of a point whose projection is at the given y coordinate
     * @param y a coordinate of the point
     * @return the latitude in radians
     */
    public static double lat(double y){
        return Math.atan(Math.sinh(Math.PI - 2*Math.PI*y));
    }

}
