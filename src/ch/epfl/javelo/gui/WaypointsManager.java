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

import java.util.function.Consumer;

/**
 * Manages the display and interaction with the waypoints
 * @author Hamane Aya (345565)
 * @author Sadgal Lina (342075)
 */
public final class WaypointsManager {

    private final Pane pane;
    private final Graph graph;
    private final ObjectProperty<MapViewParameters> mapProperty;
    private final ObservableList<Waypoint> wayPoints;
    private final ObservableList<Node> listOfGroups;
    private final Consumer<String> errorConsumer;

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
        this.errorConsumer = errorConsumer;
        //Why should it be observable?
        listOfGroups = FXCollections.observableArrayList();
        addSVGPaths();
        wayPointEvents();  //L'ordre doit matter à ce point mdr ?
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
        if (graph.nodeClosestTo(pointCh, 1000) == -1){
            errorConsumer.accept("Aucune route à proximité !");
        } else {
            wayPoints.add(new Waypoint(pointCh, graph.nodeClosestTo(pointCh, 1000)));
        }
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
                    mousePositionProperty.setValue(new Point2D(e.getX(),
                            e.getY()));
                }
            });
            listOfGroups.get(i).setOnMouseDragged(e -> {
                if (!e.isStillSincePress()) {
                    System.out.println("dragged");
                    System.out.println();
                    Point2D oldMousePosition = mousePositionProperty.get();
                    mousePositionProperty.setValue(new Point2D(e.getX() , e.getY()));
                    Point2D gap = mousePositionProperty.get().subtract(oldMousePosition);
                    listOfGroups.get(index).setLayoutX(listOfGroups.get(index).getLayoutX() + gap.getX());
                    listOfGroups.get(index).setLayoutY(listOfGroups.get(index).getLayoutY() + gap.getY());
                }
            });
            listOfGroups.get(i).setOnMouseReleased(e -> {
                if (!e.isStillSincePress()) {
                    System.out.println("released");
                    Point2D oldMousePosition = mousePositionProperty.get();
                    mousePositionProperty.setValue(new Point2D(e.getX() , e.getY()));
                    Point2D gap = mousePositionProperty.get().subtract(oldMousePosition);
                    PointWebMercator newMercatorPoint = mapProperty.get().pointAt(
                            listOfGroups.get(index).getLayoutX() + gap.getX(),
                            listOfGroups.get(index).getLayoutY() + gap.getY());
                    PointCh newPointCh = newMercatorPoint.toPointCh();
                    if(newPointCh != null && graph.nodeClosestTo(newPointCh,1000) != -1) {
                        wayPoints.set(index, new Waypoint(newPointCh, graph.nodeClosestTo(newPointCh,1000)));
                    } else {
                        addSVGPaths(); //J'etais obligée de add ça, sinon y avait un tps de
                        //latence à cause du dragged.
                        errorConsumer.accept("Aucune route à proximité !");
                    }
                }
            });
        }
    }
}