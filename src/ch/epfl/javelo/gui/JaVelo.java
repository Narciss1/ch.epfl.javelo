package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;
import static javafx.beans.binding.Bindings.when;

public final class JaVelo extends Application {


    /**
     * main method to run the application JaVelo
     * @param args the arguments are totally ignored
     */
    public static void main(String[] args) { launch(args); }


    /**
     * constructs the graphic interface of JaVelo
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        CostFunction cf = new CityBikeCF(graph);
        RouteComputer routeComputer = new RouteComputer(graph, cf);
        Path cacheBasePath = Path.of("./osm-cache");
        String tileServerHost = "tile.openstreetmap.org";

        ErrorManager errorManager = new ErrorManager();
        Consumer<String> errorConsumer = errorManager::displayError;

        RouteBean routeBean = new RouteBean(routeComputer);
        TileManager tileManager =
               new TileManager(cacheBasePath, tileServerHost);
        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager
                (graph, tileManager, routeBean, errorConsumer);

        ElevationProfileManager elevationProfileManager =
                new ElevationProfileManager(routeBean.elevationProfileProperty(),
                        routeBean.highlightedPositionProperty());

        SwitchBackgroundManager switchBackground = new SwitchBackgroundManager(annotatedMapManager,
                elevationProfileManager, cacheBasePath, errorConsumer);

        routeBean.highlightedPositionProperty().bind(
                when(annotatedMapManager.mousePositionOnRouteProperty().greaterThanOrEqualTo(0))
                        .then(annotatedMapManager.mousePositionOnRouteProperty())
                        .otherwise(elevationProfileManager.mousePositionOnProfileProperty()));

        SplitPane splitPane = new SplitPane(annotatedMapManager.pane());
        splitPane.setOrientation(Orientation.VERTICAL);
        routeBean.elevationProfileProperty().addListener(l -> {
            if(routeBean.elevationProfileProperty().get() == null) {
                splitPane.getItems().removeAll(elevationProfileManager.pane());
            } else if(!splitPane.getItems().contains(elevationProfileManager.pane())) {
                splitPane.getItems().add(elevationProfileManager.pane());
            }
        });
        SplitPane.setResizableWithParent(elevationProfileManager.pane(), false);

        StackPane mainPane = new StackPane(splitPane, errorManager.pane(),
                switchBackground.pane());

        MenuManager menuManager = new MenuManager(routeBean, annotatedMapManager,
                switchBackground);

        BorderPane borderPane = new BorderPane(mainPane, menuManager.menuBar(),
                null, null, null);

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(borderPane));
        primaryStage.setTitle("JaVelo");
        primaryStage.show();
    }
}
