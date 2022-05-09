package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.nio.file.Path;

import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.util.function.Consumer;

public final class Stage8Test extends Application {

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        Path cacheBasePath = Path.of(".");
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);

        ObjectProperty<MapViewParameters> mapViewParametersP =
                new SimpleObjectProperty<>(mapViewParameters);

        ObservableList<Waypoint> waypoints =
                FXCollections.observableArrayList(
                        new Waypoint(new PointCh(2532697, 1152350), 159049),
                        new Waypoint(new PointCh(2538659, 1154350), 117669));

        Consumer<String> errorConsumer = new ErrorConsumer();

        CostFunction cf = new CityBikeCF(graph);

        RouteComputer routeComputer = new RouteComputer(graph, cf);

        RouteBean routeBean = new RouteBean(routeComputer);

        //Sens phrase prof
        routeBean.setWaypoints(waypoints);

        routeBean.setHighlightedPosition(1000);

        RouteManager routeManager = new RouteManager(routeBean, mapViewParametersP, errorConsumer);

        WaypointsManager waypointsManager =
                new WaypointsManager(graph,
                        mapViewParametersP,
                        routeBean.waypoints(),
                        errorConsumer);

        BaseMapManager baseMapManager =
                new BaseMapManager(tileManager,
                        waypointsManager,
                        mapViewParametersP);

        ElevationProfile ec =ElevationProfileComputer.elevationProfile(routeBean.route(), 5);
        ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty = new SimpleObjectProperty<>(ec);
        ElevationProfileManager em = new ElevationProfileManager(elevationProfileProperty, routeBean.highlightedPositionProperty());
        StackPane mainPane =
                new StackPane( baseMapManager.pane(),
                        waypointsManager.pane()
                        , routeManager.pane());

       // StackPane emPane = new StackPane(em.pane());
        mainPane.getStylesheets().add("map.css");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(300);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setFullScreenExitHint("Press echap to exit full screen ta race");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.valueOf(KeyCode.ESCAPE.getName()));
        primaryStage.show();
        mainPane.requestFocus();
        mainPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F){
                primaryStage.setFullScreen(true);
            }
        });
    }

    private static final class ErrorConsumer
            implements Consumer<String> {
        @Override
        public void accept(String s) { System.out.println(s); }
    }
}
