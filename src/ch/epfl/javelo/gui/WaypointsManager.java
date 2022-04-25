package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.awt.*;
import java.util.function.Consumer;

/**
 * Manages the display and interaction with the waypoints
 * @author Hamane Aya (345565)
 * @author Sadgal Lina (342075)
 */
public final class WaypointsManager {

    private final Pane pane;
    private final Graph graph;
    private ObjectProperty<MapViewParameters> mapProperty;
    private final ObservableList<Waypoint> wayPoints;
    private final ObservableList<Node> listOfGroups;

    /**
     * Constructor
     * @param graph a graph of the road network,
     * @param mapProperty a JavaFX property containing the parameters of the displayed map
     * @param wayPoints an observable list of all the waypoints
     * @param errorConsumer an object to report errors
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapProperty,
                            ObservableList<Waypoint> wayPoints, Consumer<String> errorConsumer) {
        pane = new Pane();
        //Piazza
        pane.setPrefHeight(300);
        pane.setPrefWidth(600);
        pane.setPickOnBounds(false);
        this.graph = graph;
        this.mapProperty = mapProperty;
        this.wayPoints = wayPoints;
        //Why should it be observable?
        listOfGroups = FXCollections.observableArrayList();
        wayPointEvents();
        addSVGPaths();
        listOfGroups.addListener((InvalidationListener) e -> wayPointEvents());
        wayPoints.addListener((InvalidationListener)  l -> addSVGPaths());
        mapProperty.addListener((p, oldM, newM) -> addSVGPaths());
    }

    /**
     * Returns the panel containing the waypoints
     * @return a panel containing the waypoints
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Adds a new waypoint to the node of the graph that is closest to it
     * @param x a point's x coordinate
     * @param y a point's y coordinate
     */
    public void addWaypoint(double x, double y) {
        double e = Ch1903.e(WebMercator.lon(x), WebMercator.lat(y));
        double n = Ch1903.n(WebMercator.lon(x), WebMercator.lat(y));
        PointCh pointCh = new PointCh(e, n);
        wayPoints.add(new Waypoint(pointCh, graph.nodeClosestTo(pointCh, 1000)));
    }

    public void setMapProperty(MapViewParameters mapViewParameters){
        mapProperty.setValue(mapViewParameters);
    }

    /**
     * Position the markers at the coordinates of their corresponding waypoint
     */
    private void addSVGPaths() {

        listOfGroups.clear();
        for (int i = 0; i < wayPoints.size(); ++i) {
            Group group = new Group();
            group.getStyleClass().add("pin");
            SVGPath exterior = new SVGPath();
            SVGPath interior = new SVGPath();
            exterior.getStyleClass().add("pin_outside");
            interior.getStyleClass().add("pin_inside");
            exterior.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
            interior.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
            //Existe-il une manière d'éviter ces enchainements de if ?
            if (i == 0) {
                group.getStyleClass().add("first");
            } else if (i == wayPoints.size() - 1) {
                group.getStyleClass().add("last");
            } else {
                group.getStyleClass().add("middle");
            }
            group.getChildren().add(exterior);
            group.getChildren().add(interior);
            //Paramètre trop long.
            group.setLayoutX(mapProperty.get().viewX(PointWebMercator.ofPointCh(
                    new PointCh(wayPoints.get(i).pointCh().e(), wayPoints.get(i).pointCh().n()))));
            group.setLayoutY(mapProperty.get().viewY(PointWebMercator.ofPointCh(
                    new PointCh(wayPoints.get(i).pointCh().e(), wayPoints.get(i).pointCh().n()))));
            listOfGroups.add(group);
        }
        pane.getChildren().setAll(listOfGroups);
    }

    private void wayPointEvents(){
        for(int i = 0; i < listOfGroups.size(); ++i) {
            Waypoint waypoint = wayPoints.get(i);
            int index = i;
            listOfGroups.get(i).setOnMouseClicked( e -> {
                System.out.println("clicked");
                if (e.isStillSincePress()) {
                    wayPoints.remove(waypoint);
                }
            });
            ObjectProperty<Point2D> mousePositionProperty = new SimpleObjectProperty<>();
            listOfGroups.get(i).setOnMousePressed(e -> {
                if (!e.isStillSincePress()) {
                    mousePositionProperty.setValue(new Point2D(e.getX(), e.getY()));
                }
            });
            listOfGroups.get(i).setOnMouseReleased(e -> {
                if (!e.isStillSincePress()) {
                    System.out.println("released");
                    PointCh oldPointCh = waypoint.pointCh();
                    Point2D oldMousePosition = mousePositionProperty.get();
                    mousePositionProperty.setValue(new Point2D(e.getX(), e.getY()));
                    Point2D gap = oldMousePosition.subtract(mousePositionProperty.get());
                    PointWebMercator newMercatorPoint = mapProperty.get().pointAt(e.getX() + gap.getX(),
                            e.getY() + gap.getY());
                    PointCh newPointCh = newMercatorPoint.toPointCh();
                    if(newPointCh != null) {
                        wayPoints.set(index, new Waypoint(newPointCh, graph.nodeClosestTo(newPointCh,1000)));
                    }
                }
            });
        }
    }
}