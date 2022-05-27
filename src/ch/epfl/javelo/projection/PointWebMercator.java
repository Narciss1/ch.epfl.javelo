package ch.epfl.javelo.projection;

import static ch.epfl.javelo.Preconditions.checkArgument;
import static ch.epfl.javelo.projection.Ch1903.e;
import static ch.epfl.javelo.projection.Ch1903.n;
import static ch.epfl.javelo.projection.SwissBounds.containsEN;

/**
 * Represents a point in the Web Mercator system
 * Each point has its corresponding x and y coordinates
 * @author Aya Hamane (345565)
 */
public record PointWebMercator(double x, double y) {

    /**
     * Minimum value of zoom level
     */
    private final static int MIN_ZOOM_LEVEL = 8;
    /**
     * Minimum value of web mercator point's coordinate
     */
    private final static int MIN_COORDINATE = 0;
    /**
     * Minimum value of web mercator point's coordinate
     */
    private final static int MAX_COORDINATE = 1;

    /**
     * Compact constructor
     * @param x coordinate of the point
     * @param y coordinate of the point
     * @throws IllegalArgumentException if the coordinates are not in the range between
     * 0 and 1
     */
    public PointWebMercator {
        checkArgument(x >= MIN_COORDINATE && x <= MAX_COORDINATE &&
                y >= MIN_COORDINATE && y <= MAX_COORDINATE );
    }

    /**
     * Finds the original point that has x and y as coordinates in the zoom level given
     * @param zoomLevel level of zooming of the map
     * @param x coordinate of the point in the zoom level given
     * @param y coordinate of the point in the zoom level given
     * @return original point
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        return new PointWebMercator(Math.scalb(x, -(zoomLevel + MIN_ZOOM_LEVEL)),
                Math.scalb(y, -(zoomLevel + MIN_ZOOM_LEVEL)));
    }

    /**
     * Finds the WebMercator point corresponding to the point given in the swiss system
     * @param pointCh point in the swiss system
     * @return WebMercator point
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        double x = WebMercator.x(pointCh.lon());
        double y = WebMercator.y(pointCh.lat());
        return new PointWebMercator(x, y);
    }

    /**
     * Calculates the coordinate x in the zoom level given
     * @param zoomLevel level of zooming of the map
     * @return new coordinate x
     */
    public double xAtZoomLevel(int zoomLevel) {
        return Math.scalb(x, MIN_ZOOM_LEVEL + zoomLevel);
    }

    /**
     * Calculates the coordinate y in the zoom level given
     * @param zoomLevel level of zooming of the map
     * @return new coordinate y
     */
    public double yAtZoomLevel(int zoomLevel) {
        return Math.scalb(y, MIN_ZOOM_LEVEL + zoomLevel);
    }

    /**
     * Calculates the longitude of a point given its coordinate x
     * @return longitude in radians
     */
    public double lon() {
        return WebMercator.lon(x);
    }

    /**
     * Calculates the latitude of a point given its coordinate y
     * @return latitude in radians
     */
    public double lat() {
        return WebMercator.lat(y);
    }

    /**
     * Finds a point in the swiss system that has the same position as the receiver
     * @return point in the swiss system or null if this point is not in the limits of Switzerland
     */
    public PointCh toPointCh() {
        double e = e(lon(), lat());
        double n = n(lon(), lat());
        if (containsEN(e, n)) {
            return new PointCh(e, n);
        }
        return null;
    }
}

