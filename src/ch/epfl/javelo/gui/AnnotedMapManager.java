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
import java.util.function.Consumer;

public final class AnnotedMapManager {

    private final BaseMapManager baseMapManager;
    private final RouteManager routeManager;
    private final WaypointsManager waypointsManager;

    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private final ObjectProperty<Point2D> mousePositionP;
    private final DoubleProperty mousePositionOnRouteProperty;

    private final StackPane javeloPane;
    private final RouteBean routeBean;
    private static final MapViewParameters startMapViewParameters =
            new MapViewParameters(12, 543200, 370650);

    public AnnotedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean,
                             Consumer<String> errorConsumer) {
        mapViewParametersP = new SimpleObjectProperty<>(startMapViewParameters);
        mousePositionP = new SimpleObjectProperty<>();
        mousePositionOnRouteProperty = new SimpleDoubleProperty();

        this.routeBean = routeBean;
        waypointsManager = new WaypointsManager(graph, mapViewParametersP, routeBean.waypoints(), errorConsumer);
        baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersP);
        //est-ce que c la classe qui se charge de la rendre readOnly?
        routeManager = new RouteManager(routeBean, mapViewParametersP);
        javeloPane = new StackPane(baseMapManager.pane(), routeManager.pane(), waypointsManager.pane());
        javeloPane.getStylesheets().add("map.css");
        setMousePositionP();
        mousePositionOnRouteBinding();
    }

    public Pane pane() {
        return javeloPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }

    private void mousePositionOnRouteBinding() {
        mousePositionOnRouteProperty.bind(Bindings.createDoubleBinding((() -> {
            if(mousePositionP.get() == null) {
                return Double.NaN;
            }
            PointWebMercator webMercatorMouse = mapViewParametersP.get().pointAt(
                    mousePositionP.get().getX(),
                    mousePositionP.get().getY());
            PointCh pointChMouse = webMercatorMouse.toPointCh();
            if (routeBean.route() != null) {
                RoutePoint routePointMouse = routeBean.route().pointClosestTo(pointChMouse);
                PointWebMercator closestPoint = PointWebMercator.ofPointCh(routePointMouse.point());
                double distance = Math2.norm(
                        mapViewParametersP.get().viewX(closestPoint) - mapViewParametersP.get().viewX(webMercatorMouse),
                        mapViewParametersP.get().viewY(closestPoint) - mapViewParametersP.get().viewY(webMercatorMouse));
                if (distance <= 15) {
                    return routePointMouse.position();
                }
            }
                return Double.NaN;
        }), mousePositionP, mapViewParametersP, routeBean.routeProperty()));
    }

    private void setMousePositionP() {
        javeloPane.setOnMouseMoved(e ->  {
            mousePositionP.setValue(new Point2D(e.getX(), e.getY()));
        });

        javeloPane.setOnMouseExited(e -> {
            //Est-ce que détection legit? genre par le nul
            mousePositionP.set(null);
        });
        //Est-ce que détection legit? genre par le null
    }
}
