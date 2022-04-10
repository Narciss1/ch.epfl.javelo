package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;


public record MapViewParameters(int zoomLevel, double xCoordinate, double yCoordinate) {

    public javafx.geometry.Point2D topLeft(){
        return new Point2D(xCoordinate, yCoordinate);
    }

    public MapViewParameters withMinXY(double x, int y){
        return new MapViewParameters(zoomLevel, x, y);
    }

    public PointWebMercator pointAt(double x, double y){
        return PointWebMercator.of(zoomLevel, x, y);
    }

    public double viewX(PointWebMercator point) {
        return point.xAtZoomLevel(zoomLevel);
    }

    public double viewY(PointWebMercator point) {
        return point.yAtZoomLevel(zoomLevel);
    }

}
