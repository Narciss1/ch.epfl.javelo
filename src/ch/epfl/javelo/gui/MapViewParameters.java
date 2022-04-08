package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import com.sun.javafx.geom.Point2D;

//ATTRIBUTS DOUBLE OU INT ?
public record MapViewParameters(int zoomLevel, int xCoordinate, int yCoordinate) {

    public Point2D topLeft(){
        return new Point2D(xCoordinate,yCoordinate);
    }

    public MapViewParameters withMinXY(int x, int y){
        return new MapViewParameters(zoomLevel, x, y);
    }
    //PIAZZA
    public PointWebMercator pointAt(int x, int y){
        return PointWebMercator.of(zoomLevel, x, y);
    }

    public double viewX(PointWebMercator point) {
        return point.xAtZoomLevel(zoomLevel);
    }

    public double viewY(PointWebMercator point) {
        return point.yAtZoomLevel(zoomLevel);
    }

}
