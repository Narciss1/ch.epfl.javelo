package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import ch.epfl.javelo.parameters.Language;
import java.util.function.Consumer;

/**
 * Manages the display of the "annotated" map, i.e. the background map over which
 * the route and waypoints are superimposed
 * @author Hamane Aya (345565)
 * @author Sadgal Lina (342075)
 */
public final class AnnotatedMapManager {

    private final BaseMapManager baseMapManager;
    private final WaypointsManager waypointsManager;

    private final StackPane javeloPane;
    private final RouteBean routeBean;

    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private final ObjectProperty<Point2D> mousePositionP;
    private final DoubleProperty mousePositionOnRouteProperty;

    /**
     * The ch.epfl.javelo.parameters of the map at the start
     */
    private static final MapViewParameters startMapViewParameters =
            new MapViewParameters(12, 543200, 370650);

    /**
     * Maximum distance between the mouse pointer and the route
     */
    private static final int MAX_DISTANCE = 15;

    /**
     * Constructor
     * @param graph a road network graph
     * @param tileManager an OpenStreetMap tile manager
     * @param routeBean a route bean
     * @param errorConsumer an "error consumer" to report an error
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean,
                               Consumer<String> errorConsumer) {
        mapViewParametersP = new SimpleObjectProperty<>(startMapViewParameters);
        mousePositionP = new SimpleObjectProperty<>();
        mousePositionOnRouteProperty = new SimpleDoubleProperty();

        waypointsManager = new WaypointsManager(graph, mapViewParametersP,
                routeBean.waypoints(), errorConsumer);
        RouteManager routeManager = new RouteManager(routeBean, mapViewParametersP);
        baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersP);

        javeloPane = new StackPane(baseMapManager.pane(), routeManager.pane(),
                waypointsManager.pane());
        javeloPane.getStylesheets().add("map.css");

        this.routeBean = routeBean;
        setMousePositionP();
        mousePositionOnRouteBinding();
    }

    /**
     * Returns the panel containing the annotated map
     * @return a panel containing the annotated map
     */
    public Pane pane() {
        return javeloPane;
    }

    /**
     * Returns the property containing the position of the mouse pointer along the route
     * @return a property containing the position of the mouse pointer
     */
    public ReadOnlyDoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }

    /**
     * Binds the property containing the mouse position on the route and other
     * properties containing the map view ch.epfl.javelo.parameters, the mouse position and the route
     */
    private void mousePositionOnRouteBinding() {
        mousePositionOnRouteProperty.bind(Bindings.createDoubleBinding((() -> {
            if(mousePositionP.get() == null) {
                return Double.NaN;
            }
            PointWebMercator webMercatorMouse = mapViewParametersP.get().pointAtPointWebMercator(
                    mousePositionP.get().getX(),
                    mousePositionP.get().getY());
            PointCh pointChMouse = webMercatorMouse.toPointCh();
            if (! (routeBean.route() == null || pointChMouse == null)) {
                RoutePoint routePointMouse = routeBean.route()
                                                      .pointClosestTo(pointChMouse);
                PointWebMercator closestPoint =
                        PointWebMercator.ofPointCh(routePointMouse.point());
                double distance = Math2.norm(
                        mapViewParametersP.get().viewX(closestPoint)
                                - mapViewParametersP.get().viewX(webMercatorMouse),
                        mapViewParametersP.get().viewY(closestPoint)
                                - mapViewParametersP.get().viewY(webMercatorMouse));
                if (distance <= MAX_DISTANCE) {
                    return routePointMouse.position();
                }
            }
                return Double.NaN;
        }), mousePositionP, mapViewParametersP, routeBean.routeProperty()));
    }

    /**
     * Manages the events related to the mouse position on the annotated map
     */
    private void setMousePositionP() {
        javeloPane.setOnMouseMoved(e -> mousePositionP.setValue(new Point2D(e.getX(), e.getY())));
        javeloPane.setOnMouseExited(e -> mousePositionP.set(null));
    }

    /**
     * Sets the tile manager of the base map manager
     * @param tileManager a new tile manager
     */
    public void setTileManager(TileManager tileManager) {
        baseMapManager.setTileManager(tileManager);
    }

    public void changeLanguage(Language language) {
        waypointsManager.changeLanguage(language);
    }
}
