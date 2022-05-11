package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;


import java.util.function.Consumer;

public final class AnnotedMapManager {

    private final BaseMapManager baseMapManager;
    private final RouteManager routeManager;
    private final WaypointsManager waypointsManager;
    private final StackPane javeloPane;

    private static final MapViewParameters startMapViewParameters =
            new MapViewParameters(12, 543200, 370650);


    public AnnotedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean,
                             Consumer<String> errorConsumer) {
        ObjectProperty<MapViewParameters> mapViewParametersP =
                new SimpleObjectProperty<>(startMapViewParameters);
        waypointsManager = new WaypointsManager(graph, mapViewParametersP, routeBean.waypoints(), errorConsumer);
        baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersP);
        //est-ce que c la classe qui se charge de la rendre readOnly?
        routeManager = new RouteManager(routeBean, mapViewParametersP);
        javeloPane = new StackPane(baseMapManager.pane(), routeManager.pane(), waypointsManager.pane());
        javeloPane.getStylesheets().add("map.css");

    }

    public Pane pane() {
        return javeloPane;
    }


}
