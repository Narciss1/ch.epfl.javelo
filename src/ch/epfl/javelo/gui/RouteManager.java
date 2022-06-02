package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Edge;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineJoin;
import java.util.*;
import static java.lang.Double.isNaN;

/**
 * Manages the display of the route and a part of the interaction with it
 * @author Lina Sadgal (342075)
 * @author Aya Hamane (345565)
 */
public final class RouteManager {

    private final RouteBean routeBean;
    private final ReadOnlyObjectProperty<MapViewParameters> mapProperty;
    private final DoubleProperty lengthDiv;
    private final Pane pane;
    private final Polyline polylineItinerary;
    private final Circle circle;

    private final Map<Integer, Color> colors_correspondance = new HashMap<>();


    /**
     * Radius of the highlighted position
     */
    private final static int HIGHLIGHTED_POSITION_RADIUS = 5;

    /**
     * Constructor
     * @param routeBean a bean grouping all properties linked to the waypoints and the itinerary
     * @param mapViewParametersProperty a property containing the map view ch.epfl.javelo.parameters
     */
    public RouteManager(RouteBean routeBean,
                        ReadOnlyObjectProperty<MapViewParameters> mapViewParametersProperty) {
        colors_correspondance.put(0, Color.GREEN);
        colors_correspondance.put(1, Color.YELLOW);
        colors_correspondance.put(2, Color.ORANGE);
        colors_correspondance.put(3, Color.RED);
        colors_correspondance.put(4, Color.DARKRED);
        this.routeBean = routeBean;
        this.mapProperty = mapViewParametersProperty;
        this.lengthDiv = new SimpleDoubleProperty(2000);
        polylineItinerary = new Polyline();
        //polylineItinerary.setId("route");
        polylineItinerary.setStrokeWidth(4);
        polylineItinerary.setStrokeLineJoin(StrokeLineJoin.ROUND);
        circle = new Circle(HIGHLIGHTED_POSITION_RADIUS);
        circle.setId("highlight");
        pane = new Pane(polylineItinerary, circle);
        pane.setPickOnBounds(false);
        addListeners();
        routeEvents();
        binding();
    }

    private void binding() {
      lengthDiv.bind(Bindings.createDoubleBinding((() -> {
                  switch (mapProperty.get().zoomLevel()) {
                      case 8, 9 -> { return 1200.0; }
                      case 10 -> { return 1100.0; }
                      case 11, 12, 13 -> { return 1000.0; }
                      case 14, 15, 16, 17 -> { return 800.0; }
                      case 18, 19 -> { return 500.0; }
                      default -> { return 0.0; }
                  }
              }), mapProperty));
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
        List<Color> colors = new ArrayList<>();
        double currentElevationGain = -1;
        double nextElevationGain = 0;
        double offset = 0;
        double position = 0;
        double cap = 0;
        double averageGain = 0;
        Iterator<Edge> iEdges = routeBean.route().edges().iterator();
        for (PointCh point : pointsItinerary) {
            PointWebMercator pointMercator = PointWebMercator.ofPointCh(point);
            pointsCoordinates.add(pointMercator.xAtZoomLevel(mapProperty.get().zoomLevel()));
            pointsCoordinates.add(pointMercator.yAtZoomLevel(mapProperty.get().zoomLevel()));

            //Creer une methode auxiliaire et remplacer tt ce bordel par un case.
            if (routeBean.elevationProfile() != null) {
                if (iEdges.hasNext()) {
                    Edge currentEdge = iEdges.next();
                    if (position >= cap) {
                        cap += lengthDiv.get();
                        nextElevationGain = averageGain;
                        averageGain = 0;
                    }
                    averageGain += currentEdge.averageElevationGain();
                    position += currentEdge.length();
                }
                if (nextElevationGain < 0.1) {
                    nextElevationGain = 0;
                } else if (nextElevationGain < 0.3) {
                    nextElevationGain = 1;
                } else if (nextElevationGain < 0.5)
                    nextElevationGain = 2;
                } else if (nextElevationGain < 0.10) {
                    nextElevationGain = 3;
                } else {
                    nextElevationGain = 4;
                }
                if (nextElevationGain != currentElevationGain) {
                    colors.add(colors_correspondance.get((int)nextElevationGain));
                    currentElevationGain = nextElevationGain;
                }
            }
        Stop[] stops1 = new Stop[colors.size()];
        for (int i = 0; i < colors.size(); ++i) {
            stops1[i] = new Stop(offset, colors.get(i));
            offset = offset + (1d / (colors.size()));
        }
        LinearGradient linearGradient =
                new LinearGradient(0, 0, 1, 0, true,
                        CycleMethod.NO_CYCLE, stops1);
        polylineItinerary.getPoints().setAll(pointsCoordinates);
        polylineItinerary.setStroke(linearGradient);
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
            createCircle();
        });
        routeBean.highlightedPositionProperty().addListener(l -> createCircle());
    }

    /**
     * Manages the events related to the route
     */
    private void routeEvents() {
        circle.setOnMouseClicked(e -> {
            Point2D position = circle.localToParent(new Point2D(e.getX(), e.getY()));
            PointCh pointCh = mapProperty.get()
                    .pointAtPointCh(position.getX(), position.getY());
            int closestNode = routeBean.route()
                    .nodeClosestTo(routeBean.highlightedPosition());
            Waypoint wayPoint = new Waypoint(pointCh, closestNode);
            int index = routeBean.indexOfNonEmptySegmentAt(routeBean.highlightedPosition()) + 1;
            routeBean.waypoints().add(index, wayPoint);
        });
    }
}
