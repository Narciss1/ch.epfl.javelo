package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

    /**
     * Length of the side of a square centred on the mouse pointer
     */
    private final static int SQUARE_RADIUS = 500;

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
        addSVGPaths();
        wayPoints.addListener((InvalidationListener)  l -> addSVGPaths());
        mapProperty.addListener((p, oldM, newM) -> {
            relocateSVGPaths();
        });
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
        //Le passage est vraimnt aussi indirect que ça ? Si oui,
        //créer une méthode publique dans PointCh qui permet de faire ça mdr.
        double e = Ch1903.e(WebMercator.lon(x), WebMercator.lat(y));
        double n = Ch1903.n(WebMercator.lon(x), WebMercator.lat(y));
        PointCh pointCh = new PointCh(e, n);
        if (graph.nodeClosestTo(pointCh, SQUARE_RADIUS) == -1){
            java.awt.Toolkit.getDefaultToolkit().beep();
            errorConsumer.accept("Aucune route à proximité !");
        } else {
            wayPoints.add(new Waypoint(pointCh, graph.nodeClosestTo(pointCh, SQUARE_RADIUS)));
        }
    }

    /**
     * Position the markers at the coordinates of their corresponding waypoint
     */
    private void addSVGPaths() {
        //A améliorer et diviser en deux méthodes
        pane.getChildren().clear();
        int counting = 0;
        //Hmm... Cette boucle seems useless tbh.. We don't need les waypoints
        //we only need a specific number of groups to be add as children to the pane...
        for (Waypoint waypoint : wayPoints) {
            Group group = new Group();
            group.getStyleClass().add("pin");
            SVGPath exterior = new SVGPath();
            SVGPath interior = new SVGPath();
            exterior.getStyleClass().add("pin_outside");
            interior.getStyleClass().add("pin_inside");
            exterior.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
            interior.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
            //Existe-il une manière d'éviter ces enchainements de if ?
            if (counting == 0) {
                group.getStyleClass().add("first");
            } else if (counting == wayPoints.size() - 1) {
                group.getStyleClass().add("last");
            } else {
                group.getStyleClass().add("middle");
            }
            group.getChildren().add(exterior);
            group.getChildren().add(interior);
            pane.getChildren().add(group);
            ++counting;
        }
        relocateSVGPaths();
        //We have to call this method as well because now we are dealing with new groups
        //so these new groups must now be linked to the events.
        wayPointsEvents();

        /*for (int i = 0; i < wayPoints.size(); ++i) {
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
            //Trop long
            double newX = mapProperty.get()
                          .viewX(PointWebMercator.ofPointCh(
                          new PointCh(wayPoints.get(i).pointCh().e(), wayPoints.get(i).pointCh().n())));
            double newY = mapProperty.get()
                          .viewY(PointWebMercator.ofPointCh(
                          new PointCh(wayPoints.get(i).pointCh().e(), wayPoints.get(i).pointCh().n())));
            group.setLayoutX(newX);
            group.setLayoutY(newY);
            pane.getChildren().add(group);
        }
        wayPointsEvents();*/
    }

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
            groupsList.get(i).setLayoutX(newX);
            groupsList.get(i).setLayoutY(newY);
        }
        //pane.getChildren().stream(); idée ?
    }

    private void relocateGroup(Node group, double x, double y) {
        group.setLayoutX(x);
        group.setLayoutY(y);
    }

    /**
     *
     */
    private void wayPointsEvents(){
        ObservableList<Node> list = pane.getChildren();

        for(int i = 0; i < list.size(); ++i) {
            Waypoint waypoint = wayPoints.get(i);
            int index = i;  //Legit de faire ça pr l'index ?
            Node group = list.get(i);
//            double groupOriginalX = group.getLayoutX();
//            double groupOriginalY = group.getLayoutY();
            ObjectProperty<Point2D> mousePositionProperty = new SimpleObjectProperty<>();

            group.setOnMousePressed(e ->
                mousePositionProperty.setValue(new Point2D(e.getX(), e.getY())));

            group.setOnMouseDragged(e -> {
                Point2D oldMousePosition = mousePositionProperty.get();
                Point2D gap = new Point2D(e.getX() , e.getY()).subtract(oldMousePosition);
                //relocateGroup(group, groupOriginalX + gap.getX(),groupOriginalY + gap.getY());
                group.setLayoutX(group.getLayoutX() + gap.getX());
                group.setLayoutY(group.getLayoutY() + gap.getY());
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
                        wayPoints.set(index,
                                new Waypoint(newPointCh, graph.nodeClosestTo(newPointCh, SQUARE_RADIUS)));
                    } else {
                        //Juste pour le son!
                        java.awt.Toolkit.getDefaultToolkit().beep();
                        //addSVGPaths();
                        relocateSVGPaths();
                        errorConsumer.accept("Aucune route à proximité !");
                    }
                } else {
                    wayPoints.remove(waypoint);
                }
            });
        }
    }
}