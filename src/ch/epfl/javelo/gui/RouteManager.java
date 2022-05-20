package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.isNaN;

public final class RouteManager {

    private RouteBean routeBean;
    private ReadOnlyObjectProperty<MapViewParameters> mapProperty;
    private Pane pane;
    private final Polyline polylineItinerary;
    private Circle circle;

    private final static int HIGHLIGHTED_POSITION_RADIUS = 5;

    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParametersProperty) {
        this.routeBean = routeBean;
        this.mapProperty = mapViewParametersProperty;
        polylineItinerary = new Polyline();
        polylineItinerary.setId("route");
        circle = new Circle(HIGHLIGHTED_POSITION_RADIUS);
        circle.setId("highlight");
        pane = new Pane(polylineItinerary, circle);
        pane.setPickOnBounds(false);

        //Etant donné qu'avec javelo initialement tout est vide est-ce qu'il faut tej ses trucs ou les garder?

//        createPolyline();
//        createCircle();

        addListeners();
        routeEvents();
    }

    public Pane pane() {
        return pane;
    }

    private void createPolyline() {
        if (routeBean.route() == null){
            polylineItinerary.setVisible(false);
            return;
        } else {
            polylineItinerary.setVisible(true);
        }
        List<PointCh> pointsItinerary = routeBean.route().points();
        List<Double> pointsCoordinates = new ArrayList<>();
        for (PointCh point : pointsItinerary) {
            PointWebMercator pointMercator = PointWebMercator.ofPointCh(point);
            pointsCoordinates.add(pointMercator.xAtZoomLevel(mapProperty.get().zoomLevel()));
            pointsCoordinates.add(pointMercator.yAtZoomLevel(mapProperty.get().zoomLevel()));
        }
        polylineItinerary.getPoints().setAll(pointsCoordinates);
        moveItinerary();
    }

    private void moveItinerary() {
        polylineItinerary.setLayoutX(-mapProperty.get().xCoordinate());
        polylineItinerary.setLayoutY(-mapProperty.get().yCoordinate());
    }

    private void createCircle() {
        double position = routeBean.highlightedPosition();
        if(routeBean.route() == null || isNaN(position)) {
            circle.setVisible(false);
            return;
        }
        circle.setVisible(true);
        PointWebMercator pointWebMercatorHighlightedPosition = PointWebMercator.ofPointCh(
                routeBean.route().pointAt(position));
        circle.setLayoutX(mapProperty.get().viewX(pointWebMercatorHighlightedPosition));
        circle.setLayoutY(mapProperty.get().viewY(pointWebMercatorHighlightedPosition));
    }

    private void routeEvents() {
        circle.setOnMouseClicked(e -> {

            //ces checks avec le if sont nécessaires (le programme marche pas sans), est-ce normal ?
            if(routeBean.route() != null && !isNaN(routeBean.highlightedPosition())) {
                Point2D position = circle.localToParent(new Point2D(e.getX(), e.getY()));
                //Je pense qu'on a vrmt besoin d'une version PointCh de pointAt pr le coup.
                PointWebMercator pointMercator = mapProperty.get().pointAt(position.getX(), position.getY());
                PointCh pointCh = pointMercator.toPointCh();
                int closestNode = routeBean.route().nodeClosestTo(routeBean.highlightedPosition());
                Waypoint wayPoint = new Waypoint(pointCh, closestNode);
                int index = routeBean.indexOfNonEmptySegmentAt(routeBean.highlightedPosition()) + 1;
                routeBean.waypoints().add(index, wayPoint);
            }
        });
    }

    private void addListeners() {
        mapProperty.addListener((p, oldM, newM) -> {
            if (oldM.zoomLevel() == newM.zoomLevel()) {
                moveItinerary();
            } else {
                createPolyline();
            }
            createCircle();
        });
        routeBean.routeProperty().addListener( l -> {
            createPolyline();
            //createCircle(); pour moi techniquement useless mais sur piazza Albinos dit qu'il le faut (@1369_f2)
        });
        routeBean.highlightedPositionProperty().addListener(l -> {
            // Est-ce qu'on doit createPolyline quand la position change?
            createCircle();
            //createPolyline(); pour moi useless.
        });
    }
}
