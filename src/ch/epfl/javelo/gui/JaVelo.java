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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class JaVelo extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("ch_west"));
        Path cacheBasePath = Path.of(".");
        String tileServerHost = "tile.openstreetmap.org";
        ErrorManager errorManager = new ErrorManager();
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);
        CostFunction cf = new CityBikeCF(graph);
        RouteComputer routeComputer = new RouteComputer(graph, cf);
        RouteBean routeBean = new RouteBean(routeComputer);
        Consumer<String> errorConsumer = errorManager::displayError;
        AnnotedMapManager annotedMapManager = new AnnotedMapManager(graph, tileManager, routeBean, errorConsumer);

        ElevationProfileManager profileManager =
                new ElevationProfileManager(routeBean.elevationProfileProperty(), routeBean.highlightedPositionProperty());


        SplitPane splitPane = new SplitPane(annotedMapManager.pane());

        splitPane.setOnMouseMoved(e -> {
            if(annotedMapManager.mousePositionOnRouteProperty().get() >= 0) {
                routeBean.highlightedPositionProperty().bind(annotedMapManager.mousePositionOnRouteProperty());
            } else {
                routeBean.highlightedPositionProperty().bind(profileManager.mousePositionOnProfileProperty());
            }
        });

        routeBean.elevationProfileProperty().addListener(l -> {
            if(routeBean.elevationProfileProperty().get() == null) {
                splitPane.getItems().removeAll(profileManager.pane());
            } else if(!splitPane.getItems().contains(profileManager.pane())) {
                splitPane.getItems().add(profileManager.pane());
            }
        });

        splitPane.setOrientation(Orientation.VERTICAL);
        SplitPane.setResizableWithParent(profileManager.pane(), false);

        MenuItem gpxExporter = new MenuItem("Exporter GPX");
        MenuBar menuBar = new MenuBar(new Menu("Fichier", null, gpxExporter));
        //menuBar.setUseSystemMenuBar(true);

        gpxExporter.disableProperty().bind(Bindings.createBooleanBinding(
                () -> {
                    if (routeBean.routeProperty() == null) {
                        return true;
                    } else {
                        return false;   //Est-ce que ce else est nécessaire ou elle est par défaut à false ?? :aaa:
                    }
                }, routeBean.routeProperty()
        ));

        gpxExporter.setOnAction(e  -> {
            try {
                GpxGenerator.writeGpx("Javelo.gpx", routeBean.route(), routeBean.elevationProfile());
            }
            catch(IOException exception) {
                throw new UncheckedIOException(exception);
            }
        });

        BorderPane borderPane = new BorderPane(splitPane, menuBar, null, null, null);
        StackPane mainPain = new StackPane(borderPane, errorManager.pane());
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(mainPain));
        primaryStage.setTitle("JaVelo");
        primaryStage.show();
    }
}
