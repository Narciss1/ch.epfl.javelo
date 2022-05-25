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

/**
 * Manages the display of the route and a part of the interaction with it
 * @author Lina Sadgal (342075)
 * @author Aya Hamane (345565)
 */
public final class RouteManager {

    private final RouteBean routeBean;
    private final ReadOnlyObjectProperty<MapViewParameters> mapProperty;
    private final Pane pane;
    private final Polyline polylineItinerary;
    private final Circle circle;

    /**
     * Radius of the highlighted position
     */
    private final static int HIGHLIGHTED_POSITION_RADIUS = 5;

    /**
     * Constructor
     * @param routeBean a bean grouping all properties linked to the waypoints and the itinerary
     * @param mapViewParametersProperty a property containing the map view parameters
     */
    public RouteManager(RouteBean routeBean,
                        ReadOnlyObjectProperty<MapViewParameters> mapViewParametersProperty) {
        this.routeBean = routeBean;
        this.mapProperty = mapViewParametersProperty;
        polylineItinerary = new Polyline();
        polylineItinerary.setId("route");
        circle = new Circle(HIGHLIGHTED_POSITION_RADIUS);
        circle.setId("highlight");
        pane = new Pane(polylineItinerary, circle);
        pane.setPickOnBounds(false);
        /*//Etant donné qu'avec javelo initialement tout est vide est-ce qu'il faut tej ses trucs ou les garder?
        createPolyline();
        createCircle();*/
        addListeners();
        routeEvents();
    }

    /**
     * Returns the JavaFX panel displaying the route
     * @return a JavaFX panel displaying the route
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Creates the polyline representing the route
     */
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

    /**
     * Relocates the itinerary
     */
    private void moveItinerary() {
        polylineItinerary.setLayoutX(-mapProperty.get().xCoordinate());
        polylineItinerary.setLayoutY(-mapProperty.get().yCoordinate());
    }

    /**
     * Creates the circle representing the highlighted position
     */
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

    /**
     * Adds listeners to route manager
     */
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

    /**
     * Manages the events related to the route
     */
    private void routeEvents() {
        circle.setOnMouseClicked(e -> {
            //ces checks avec le if sont nécessaires (le programme marche pas sans), est-ce normal ?
            if(routeBean.route() != null && !isNaN(routeBean.highlightedPosition())) {
                Point2D position = circle.localToParent(new Point2D(e.getX(), e.getY()));
                PointCh pointCh = mapProperty.get()
                        .pointAtPointCh(position.getX(), position.getY());
                int closestNode = routeBean.route()
                                           .nodeClosestTo(routeBean.highlightedPosition());
                Waypoint wayPoint = new Waypoint(pointCh, closestNode);
                int index = routeBean.indexOfNonEmptySegmentAt(routeBean.highlightedPosition()) + 1;
                routeBean.waypoints().add(index, wayPoint);
            }
        });
    }

}
