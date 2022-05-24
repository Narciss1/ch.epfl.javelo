package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;
import static javafx.beans.binding.Bindings.when;

public final class JaVelo extends Application {


    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        Path cacheBasePath = Path.of("./osm-cache");
        String tileServerHost = "tile.openstreetmap.org";
        ErrorManager errorManager = new ErrorManager();
        TileManager tileManager =
               new TileManager(cacheBasePath, tileServerHost);
        CostFunction cf = new CityBikeCF(graph);
        RouteComputer routeComputer = new RouteComputer(graph, cf);
        RouteBean routeBean = new RouteBean(routeComputer);
        Consumer<String> errorConsumer = errorManager::displayError;
        AnnotatedMapManager annotedMapManager = new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer);
        BooleanProperty positiveMousePositionOnRoute = new SimpleBooleanProperty();
        ElevationProfileManager profileManager =
                new ElevationProfileManager(routeBean.elevationProfileProperty(), routeBean.highlightedPositionProperty());

        positiveMousePositionOnRoute.bind(Bindings.createBooleanBinding(
                () -> {
                    if (annotedMapManager.mousePositionOnRouteProperty().get() >= 0) {
                        return true;
                    } else {
                        return false;
                    }
                }, annotedMapManager.mousePositionOnRouteProperty()
        ));

        Label label = new Label("Tiles Format");
        RadioButton osmButton = new RadioButton("OpenStreetMap");
        RadioButton cyclosmButton = new RadioButton("CyclOSM");
        osmButton.setSelected(true);

        TilePane tilePane = new TilePane(Orientation.VERTICAL);
        tilePane.maxHeightProperty().bind(Bindings.createDoubleBinding(() ->
            osmButton.getHeight() + cyclosmButton.getHeight() + label.getHeight()
        , osmButton.heightProperty(), cyclosmButton.heightProperty(), label.heightProperty()));
        tilePane.maxWidthProperty().bind(Bindings.createDoubleBinding(() ->
                   Math.max(Math.max(osmButton.getWidth(), cyclosmButton.getWidth()),
                            label.getWidth())
                , osmButton.widthProperty(), cyclosmButton.widthProperty()));
        tilePane.setPickOnBounds(false);
        tilePane.setBackground(Background.fill(Color.MINTCREAM));
        ToggleGroup tileButtons = new ToggleGroup();

        osmButton.setToggleGroup(tileButtons);
        cyclosmButton.setToggleGroup(tileButtons);

        tilePane.getChildren().add(label);
        tilePane.getChildren().add(osmButton);
        tilePane.getChildren().add(cyclosmButton);
        tilePane.setTileAlignment(Pos.CENTER_LEFT);
        Pane switchOsm = new Pane(tilePane);
        switchOsm.setPickOnBounds(false);

        SplitPane splitPane = new SplitPane(annotedMapManager.pane());

        routeBean.elevationProfileProperty().addListener(l -> {
            if(routeBean.elevationProfileProperty().get() == null) {
                splitPane.getItems().removeAll(profileManager.pane());
            } else if(!splitPane.getItems().contains(profileManager.pane())) {
                splitPane.getItems().add(profileManager.pane());
            }
        });

        routeBean.highlightedPositionProperty().bind(
                when(positiveMousePositionOnRoute)
                .then(annotedMapManager.mousePositionOnRouteProperty())
                        .otherwise(profileManager.mousePositionOnProfileProperty()));

        splitPane.setOrientation(Orientation.VERTICAL);
        SplitPane.setResizableWithParent(profileManager.pane(), false);

        MenuItem gpxExporter = new MenuItem("Exporter GPX");
        MenuBar menuBar = new MenuBar(new Menu("Fichier", null, gpxExporter));

        gpxExporter.disableProperty().bind(Bindings.createBooleanBinding(
                () -> {
                    if (routeBean.route() == null) {
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

        osmButton.setOnAction( e -> {
            annotedMapManager.setTileManager(new TileManager(cacheBasePath,"tile.openstreetmap.org"));
        });
        cyclosmButton.setOnAction( e -> {
            annotedMapManager.setTileManager(new TileManager(cacheBasePath,"a.tile-cyclosm.openstreetmap.fr/cyclosm"));
        });

        //BorderPane sûrement à changer.
        BorderPane borderPane = new BorderPane(splitPane, menuBar, null, null, null);
        tilePane.layoutYProperty().bind(Bindings.createDoubleBinding(
                () -> menuBar.getHeight(),  menuBar.heightProperty()
        ));
        tilePane.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> switchOsm.getWidth() - tilePane.getWidth(),  switchOsm.widthProperty(), tilePane.widthProperty()
        ));
        StackPane mainPane = new StackPane(borderPane, errorManager.pane(), switchOsm);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(mainPane));
        mainPane.requestFocus();
        mainPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F){
                primaryStage.setFullScreen(true);
            }
        });
        primaryStage.setTitle("JaVelo");
        primaryStage.setFullScreenExitHint("Press escape to exit full screen");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.valueOf(KeyCode.ESCAPE.getName()));
        primaryStage.show();
    }
}
