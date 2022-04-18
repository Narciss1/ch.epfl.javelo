package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * Represents the parameters of the map background for the graphic interface
 * @author Hamane Aya (345565)
 * @author Sadgal Lina (342075)
 */
public record MapViewParameters(int zoomLevel, double xCoordinate, double yCoordinate) {

    /**
     * creates a Point2D using the coordinates of the point in the top left of the
     background map is located
     * @return a Point2D located where the point in the top left of the
     * background map is located
     */
    public javafx.geometry.Point2D topLeft(){
        return new Point2D(xCoordinate, yCoordinate);
    }

    /**
     * creates a MapViewParameters identical to the receptor but with a different point
     * in the top left corner
     * @param x the coordinate x of the point in the top left corner
     * @param y the coordinate y of the point in the top left corner
     * @return an object of type MapViewParameters identical to the receptor yet the coordinates of the
     * point in the top left corner are different
     */
    public MapViewParameters withMinXY(double x, int y){
        return new MapViewParameters(zoomLevel, x, y);
    }

    /**
     * gives us a point as an instance of PointWebMercator through its coordinates compared
     to the point in the top left corner of the map background
     * @param x the coordinate x of the point compared to the point
     in the top left corner of the map background
     * @param y the coordinate y of the point compared to the point
     in the top left corner of the map background
     * @return an object of type PointWebMercator given its coordinates in comparison of
     */
    public PointWebMercator pointAt(double x, double y){
        return PointWebMercator.of(zoomLevel, x, y);
    }

    /**
     * calculates the coordinate x of the PointWebMercator given in comparison of the point
     in the top left corner.
     * @param point the PointWebMercator whose coordinate x in the map background we want.
     * @return the coordinate x of the PointWebMercator given in comparison of the point
    in the top left corner.
     */
    public double viewX(PointWebMercator point) {
        return point.xAtZoomLevel(zoomLevel) - xCoordinate;
    }

    /**
     * calculates the coordinate y of the PointWebMercator given in comparison of the point
     in the top left corner.
     * @param point the PointWebMercator whose coordinate y in the map background we want.
     * @return the coordinate y of the PointWebMercator given in comparison of the point
     in the top left corner.
     */
    public double viewY(PointWebMercator point) {
        return point.yAtZoomLevel(zoomLevel) - yCoordinate;
    }

}
