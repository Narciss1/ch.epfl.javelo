package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

public final class JaVelo extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        Path cacheBasePath = Path.of(".");
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);
        CostFunction cf = new CityBikeCF(graph);
        RouteComputer routeComputer = new RouteComputer(graph, cf);
        RouteBean routeBean = new RouteBean(routeComputer);
        Consumer<String> errorConsumer = new ErrorConsumer();
        AnnotedMapManager annotedMapManager = new AnnotedMapManager(graph, tileManager, routeBean, errorConsumer);

        ElevationProfileManager profileManager =
                new ElevationProfileManager(routeBean.elevationProfileProperty(), routeBean.highlightedPositionProperty());


        SplitPane splitPane = new SplitPane(annotedMapManager.pane());
//        if(annotedMapManager.mousePositionOnRouteProperty().get() >= 0) {
//            System.out.println("bind annoted");
//            routeBean.highlightedPositionProperty().bind(annotedMapManager.mousePositionOnRouteProperty());
//        } else {
//            System.out.println("bind profile");
//            routeBean.highlightedPositionProperty().bind(profileManager.mousePositionOnProfileProperty());
//        }
        splitPane.setOnMouseMoved(e -> {
            if(annotedMapManager.mousePositionOnRouteProperty().get() >= 0) {
                routeBean.highlightedPositionProperty().bind(annotedMapManager.mousePositionOnRouteProperty());
            } else {
                routeBean.highlightedPositionProperty().bind(profileManager.mousePositionOnProfileProperty());
            }
        });

        //routeBean.highlightedPositionProperty().bind(profileManager.mousePositionOnProfileProperty());
        //routeBean.highlightedPositionProperty().bind(annotedMapManager.mousePositionOnRouteProperty());


       /* Bindings.bindBidirectional(annotedMapManager.mousePositionOnRouteProperty(),
                profileManager.mousePositionOnProfileProperty() );
        */
        /* routeBean.highlightedPositionProperty().bind(Bindings.createDoubleBinding(() -> {
            if(annotedMapManager.mousePositionOnRouteProperty().get() >= 0) {
                return 5d;
                        //annotedMapManager.mousePositionOnRouteProperty().get();
             }
                return 3d;
                        //profileManager.mousePositionOnProfileProperty().get();
            }, annotedMapManager.mousePositionOnRouteProperty(), profileManager.mousePositionOnProfileProperty()));
*/

        routeBean.elevationProfileProperty().addListener(l -> {
            if(routeBean.elevationProfileProperty().get() == null) {
                splitPane.getItems().removeAll(profileManager.pane());
            } else if(!splitPane.getItems().contains(profileManager.pane())) {
                splitPane.getItems().add(profileManager.pane());
            }
        });
        splitPane.setOrientation(Orientation.VERTICAL);
        SplitPane.setResizableWithParent(profileManager.pane(), false);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(splitPane));
        primaryStage.setTitle("JaVelo");
        primaryStage.show();
    }

    private static final class ErrorConsumer
            implements Consumer<String> {
        @Override
        public void accept(String s) { System.out.println(s); }
    }
}
