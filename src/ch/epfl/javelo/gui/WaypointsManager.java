package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
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
    private final Consumer<String> errorConsumer;
    //private final AudioClip delete = new AudioClip(
                           // getClass().getResource("/delete.wav").toString());

    /**
     * Length of the side of a square centred on the mouse pointer
     */
    private final static int SQUARE_RADIUS = 500;
    private final static String NO_ROUTE = "Aucune route à proximité !";
    private final static String NOT_SWITZERLAND = "À l'extérieur de la Suisse !";
    private final static String EXTERIOR = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private final static String INTERIOR = "M0-23A1 1 0 000-29 1 1 0 000-23";
    private final static String OUTSIDE = "pin_outside";
    private final static String INSIDE = "pin_inside";
    private final static String PIN = "pin";
    private final static String FIRST = "first";
    private final static String MIDDLE = "middle";
    private final static String LAST = "last";

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
        pane.setPickOnBounds(false);
        this.graph = graph;
        this.mapProperty = mapProperty;
        this.wayPoints = wayPoints;
        this.errorConsumer = errorConsumer;
        addListeners();
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
        PointWebMercator pointWebMercator = new PointWebMercator(x, y);
        PointCh pointCh = pointWebMercator.toPointCh();
        if (pointCh == null) {
            errorConsumer.accept(NOT_SWITZERLAND);
        } else if (graph.nodeClosestTo(pointCh, SQUARE_RADIUS) == -1) {
            errorConsumer.accept(NO_ROUTE);
        } else {
            wayPoints.add(new Waypoint(pointCh, graph.nodeClosestTo(pointCh, SQUARE_RADIUS)));
        }
    }

    /**
     * Reverses the order of the wayPoints in their list in order to reverse the itinerary
     */
    //Extension
    public void reverseItinerary() {
        FXCollections.reverse(wayPoints);
    }

    /**
     * Clears the list of wayPoints in order to remove the itinerary
     */
    //Extension
    public void removeItinerary() { wayPoints.clear(); }

    /**
     * Positions the markers at the coordinates of their corresponding waypoint
     */
    private void addSVGPaths() {
        pane.getChildren().clear();
        int counting = 0;
        for(int i = 0; i < wayPoints.size(); ++i) {
            Group group = new Group();
            group.getStyleClass().add(PIN);

            SVGPath exterior = new SVGPath();
            SVGPath interior = new SVGPath();
            exterior.getStyleClass().add(OUTSIDE);
            interior.getStyleClass().add(INSIDE);
            exterior.setContent(EXTERIOR);
            interior.setContent(INTERIOR);

            if (counting == 0) {
                group.getStyleClass().add(FIRST);
            } else if (counting == wayPoints.size() - 1) {
                group.getStyleClass().add(LAST);
            } else {
                group.getStyleClass().add(MIDDLE);
            }
            ++counting;

            group.getChildren().add(exterior);
            group.getChildren().add(interior);
            pane.getChildren().add(group);

            wayPointsEvents(group, wayPoints.get(i), i);
        }
        relocateSVGPaths();
    }

    /**
     * Relocates all groups representing markers
     */
    private void relocateSVGPaths(){
        ObservableList<Node> groupsList = pane.getChildren();
        for (int i = 0; i < groupsList.size(); ++i) {
            Waypoint groupWaypoint = wayPoints.get(i);
            double eWaypoint = groupWaypoint.pointCh().e();
            double nWaypoint = groupWaypoint.pointCh().n();
            double newX = mapProperty.get().viewX(PointWebMercator.ofPointCh(
                    new PointCh(eWaypoint, nWaypoint)));
            double newY = mapProperty.get().viewY(PointWebMercator.ofPointCh(
                    new PointCh(eWaypoint, nWaypoint)));
            relocateGroup(groupsList.get(i), newX, newY);
        }
    }

    /**
     * Relocates a group representing a marker
     * @param group a group representing a marker
     * @param x the group's x coordinate
     * @param y the group's y coordinate
     */
    private void relocateGroup(Node group, double x, double y) {
        group.setLayoutX(x);
        group.setLayoutY(y);
    }

    /**
     * Adds listeners to waypoints manager
     */
    private void addListeners() {
        wayPoints.addListener((InvalidationListener) l -> addSVGPaths());
        mapProperty.addListener((p, oldM, newM) -> relocateSVGPaths());
    }

    /**
     *
     * @param group
     * @param wayPoint
     * @param i
     */
    private void wayPointsEvents(Group group, Waypoint wayPoint, int i){
            ObjectProperty<Point2D> mousePositionProperty = new SimpleObjectProperty<>();

            group.setOnMousePressed(e ->
                    mousePositionProperty.setValue(new Point2D(e.getX(), e.getY())));

            group.setOnMouseDragged(e -> {
                Point2D oldMousePosition = mousePositionProperty.get();
                Point2D gap = new Point2D(e.getX() , e.getY()).subtract(oldMousePosition);
                double groupOriginalX = group.getLayoutX();
                double groupOriginalY = group.getLayoutY();
                relocateGroup(group, groupOriginalX + gap.getX(),groupOriginalY + gap.getY());
            });

            group.setOnMouseReleased(e -> {
                if (!e.isStillSincePress()) {
                    Point2D oldMousePosition = mousePositionProperty.get();
                    mousePositionProperty.setValue(new Point2D(e.getX() , e.getY()));
                    Point2D gap = mousePositionProperty.get().subtract(oldMousePosition);

                    PointWebMercator newMercatorPoint = mapProperty.get().pointAt(
                            group.getLayoutX() + gap.getX(),
                            group.getLayoutY() + gap.getY());
                    PointCh newPointCh = newMercatorPoint.toPointCh();

                    if(newPointCh != null && graph.nodeClosestTo(newPointCh, SQUARE_RADIUS) != -1) {
                        wayPoints.set(i,
                                new Waypoint(newPointCh, graph.nodeClosestTo(newPointCh, SQUARE_RADIUS)));
                    } else {
                        relocateSVGPaths();
                        errorConsumer.accept(NO_ROUTE);
                    }
                } else {
                    //delete.play();
                    wayPoints.remove(wayPoint);
                }
            });
        }
}