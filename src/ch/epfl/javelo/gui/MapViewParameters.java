package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * Represents the ch.epfl.javelo.parameters of the map background for the graphic interface
 * @author Hamane Aya (345565)
 * @author Sadgal Lina (342075)
 */
public record MapViewParameters(int zoomLevel, double xCoordinate, double yCoordinate) {

    /**
     * Creates a Point2D using the coordinates of the point in the top left of the
     * background map is located
     * @return a Point2D located where the point in the top left of the
     * background map is located
     */
    public Point2D topLeft() {
        return new Point2D(xCoordinate, yCoordinate);
    }

    /**
     * Creates a MapViewParameters identical to the receptor but with a different point
     * in the top left corner
     * @param x the coordinate x of the point in the top left corner
     * @param y the coordinate y of the point in the top left corner
     * @return an object of type MapViewParameters identical to the receptor yet the coordinates of the
     * point in the top left corner are different
     */
    public MapViewParameters withMinXY(double x, double y) {
        return new MapViewParameters(zoomLevel, x, y);
    }

    /**
     * Gives us a point as an instance of PointWebMercator through its coordinates compared
     * to the point in the top left corner of the map background
     * @param x the coordinate x of the point compared to the point
     * in the top left corner of the map background
     * @param y the coordinate y of the point compared to the point
     * in the top left corner of the map background
     * @return an object of type PointWebMercator given its coordinates in comparison of the point
     * in the top left corner.
     */
    public PointWebMercator pointAtPointWebMercator(double x, double y) {
        return PointWebMercator.of(zoomLevel, x + xCoordinate, y + yCoordinate);
    }

    /**
     * Gives us a point as an instance of PointCh through its coordinates compared
     * to the point in the top left corner of the map background
     * @param x the coordinate x of the point compared to the point
     * in the top left corner of the map background
     * @param y the coordinate y of the point compared to the point
     * in the top left corner of the map background
     * @return an object of type PointCh given its coordinates in comparison of the point
     * in the top left corner.
     */
    public PointCh pointAtPointCh(double x, double y) {
        return pointAtPointWebMercator(x, y).toPointCh();
    }

    /**
     * Calculates the coordinate x of the PointWebMercator given in comparison of the point
     * in the top left corner.
     * @param point the PointWebMercator whose coordinate x in the map background we want.
     * @return the coordinate x of the PointWebMercator given in comparison of the point
     * in the top left corner.
     */
    public double viewX(PointWebMercator point) {
        return point.xAtZoomLevel(zoomLevel) - xCoordinate;
    }

    /**
     * Calculates the coordinate y of the PointWebMercator given in comparison of the point
     * in the top left corner.
     * @param point the PointWebMercator whose coordinate y in the map background we want.
     * @return the coordinate y of the PointWebMercator given in comparison of the point
     * in the top left corner.
     */
    public double viewY(PointWebMercator point) {
        return point.yAtZoomLevel(zoomLevel) - yCoordinate;
    }
}
