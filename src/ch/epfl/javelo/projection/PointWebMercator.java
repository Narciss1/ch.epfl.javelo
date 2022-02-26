package ch.epfl.javelo.projection;

import static ch.epfl.javelo.Preconditions.checkArgument;
import static ch.epfl.javelo.projection.Ch1903.e;
import static ch.epfl.javelo.projection.Ch1903.n;
import static ch.epfl.javelo.projection.SwissBounds.containsEN;

public record PointWebMercator(double x, double y) {

    /**
     * Compact constructor
     *
     * @param x a coordinate of the point
     * @param y a coordinate of the point
     */
    public PointWebMercator {
        checkArgument(x >= 0 && x <= 1 && y >= 0 && y <= 1 );
    }

    /**
     * Finds the original point that has x and y as coordinates in the zoom level given
     * @param zoomLevel level of zooming of the map
     * @param x a coordinate of the point in the zoom level given
     * @param y a coordinate of the point in the zoom level given
     * @return the original point
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        return new PointWebMercator(Math.scalb(x, -zoomLevel), Math.scalb(y, -zoomLevel));
    }

    /**
     * Finds the Webmercator point corresponding to the point given in the swiss system
     * @param pointCh a point in the swiss system
     * @return a WebMercator point
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        double x = WebMercator.x(pointCh.lon());
        double y = WebMercator.y(pointCh.lat());
        return new PointWebMercator(x, y);
    }

    /**
     * Calculates the coordinate x in the zoom level given
     *
     * @param zoomLevel level of zooming of the map
     * @return the new coordiante x
     */
    public double xAtZoomLevel(int zoomLevel) {
        return Math.scalb(x, zoomLevel);
    }

    /**
     * Calculates the coordinate y in the zoom level given
     *
     * @param zoomLevel level of zooming of the map
     * @return the new coordiante y
     */
    public double yAtZoomLevel(int zoomLevel) {
        return Math.scalb(y, zoomLevel);
    }

    /**
     * Calculates the longitude of a point given its coordinate x
     *
     * @return the longitude in radians
     */
    public double lon() {
        return WebMercator.lon(x);
    }

    /**
     * Calculates the latitude of a point given its coordinate y
     *
     * @return the latitude in radians
     */
    public double lat() {
        return WebMercator.lat(y);
    }

    /**
     * Finds a point in the swiss system that has the same position as the receiver
     * @return a point in the swiss system or null if this point is not in the limits of Switzerland
     */
    public PointCh toPointCh() {
        double e = e(WebMercator.lon(this.x), WebMercator.lat(this.y));
        double n = n(WebMercator.lon(this.x), WebMercator.lat(this.y));
        if (containsEN(e, n)) {
            return new PointCh(e, n);
        } else {
            return null;
        }
    }
}

