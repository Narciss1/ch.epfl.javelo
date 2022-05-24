package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
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

    //On est d'accord que cette méthode n'a pas à être commentée ? (Override)
    @Override
    public void start(Stage primaryStage) throws Exception {

        //Pour cette sucession de trucs j'ai essayé un peu de garder une cohérence dans
        //leur succession ms je vois pas cmt améliorer autrement.

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
        AnnotedMapManager annotedMapManager = new AnnotedMapManager
                (graph, tileManager, routeBean, errorConsumer);

        ElevationProfileManager elevationProfileManager =
                new ElevationProfileManager(routeBean.elevationProfileProperty(),
                        routeBean.highlightedPositionProperty());

        routeBean.highlightedPositionProperty().bind(
                when(annotedMapManager.mousePositionOnRouteProperty().greaterThanOrEqualTo(0))
                        .then(annotedMapManager.mousePositionOnRouteProperty())
                        .otherwise(elevationProfileManager.mousePositionOnProfileProperty()));

        StackPane mainPane = new StackPane(createSplitPane(annotedMapManager, elevationProfileManager,
                routeBean), errorManager.pane(),
                changeTilesPane(annotedMapManager, cacheBasePath, routeBean));
        BorderPane borderPane = new BorderPane(mainPane, exporter(routeBean),
        null, null, null);

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(borderPane));
        borderPane.requestFocus();
        borderPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F){
                primaryStage.setFullScreen(true);
            }
        });
        primaryStage.setTitle("JaVelo");
        primaryStage.setFullScreenExitHint("Appuyez sur echap pour quitter le plein écran");
        primaryStage.setFullScreenExitKeyCombination(
                KeyCombination.valueOf(KeyCode.ESCAPE.getName()));
        primaryStage.show();
    }

    /**
     * create a splitpane always containing the pane from annotedMapManager and the pane
     * from ElevationProfileManager only when there is an existing route.
     * @param annotedMapManager the annotedMapManager of JaVelo
     * @param elevationProfileManager the elevationProfileManager of JaVelo
     * @param routeBean the routeBean of Javelo
     * @return the splitpane for JaVelo using the annotedMapManager, the elevationProfileManager,
     * and the routeBean.
     */
    private static SplitPane createSplitPane(AnnotedMapManager annotedMapManager,
                                             ElevationProfileManager elevationProfileManager,
                                             RouteBean routeBean) {
        SplitPane splitPane = new SplitPane(annotedMapManager.pane());
        splitPane.setOrientation(Orientation.VERTICAL);
        SplitPane.setResizableWithParent(elevationProfileManager.pane(), false);
        routeBean.elevationProfileProperty().addListener(l -> {
            if(routeBean.elevationProfileProperty().get() == null) {
                splitPane.getItems().removeAll(elevationProfileManager.pane());
            } else if(!splitPane.getItems().contains(elevationProfileManager.pane())) {
                splitPane.getItems().add(elevationProfileManager.pane());
            }
        });
        return splitPane;
    }


    //Pour l'argument ici est-ce que c'est mieux de passer tout le routeBean ou de passer 2
    //arguments, un pour route, l'autre pour elevationProfile
    private static MenuBar exporter(RouteBean routeBean) {

        MenuItem gpxExporter = new MenuItem("Exporter GPX");
        MenuBar menuBar = new MenuBar(new Menu("Fichier", null, gpxExporter));

        gpxExporter.disableProperty().bind(Bindings.createBooleanBinding(
                () -> routeBean.route() == null, routeBean.routeProperty()));

        gpxExporter.setOnAction(e  -> {
            try {
                GpxGenerator.writeGpx("Javelo.gpx", routeBean.route(),
                        routeBean.elevationProfile());
            }
            catch(IOException exception) {
                throw new UncheckedIOException(exception);
            }
        });

        return menuBar;
    }


    //Créer une classe changeTilesManager pour ce panneau.
    private static Pane changeTilesPane(AnnotedMapManager annotedMapManager,
                                        Path cacheBasePath, RouteBean routeBean) {
        Label label = new Label("Fond de carte");
        RadioButton osmButton = new RadioButton("OpenStreetMap");
        //Façon très moche de procéder Aya.
        RadioButton cyclosmButton = new RadioButton("CyclOSM           ");
        osmButton.setSelected(true);

        osmButton.setOnAction( e -> annotedMapManager.setTileManager(
                    new TileManager(cacheBasePath,"tile.openstreetmap.org")));
        cyclosmButton.setOnAction( e -> annotedMapManager.setTileManager(
                    new TileManager(cacheBasePath,"a.tile-cyclosm.openstreetmap.fr/cyclosm")));

        ToggleGroup tileButtons = new ToggleGroup();
        osmButton.setToggleGroup(tileButtons);
        cyclosmButton.setToggleGroup(tileButtons);

        TilePane tilePane = new TilePane(label, osmButton, cyclosmButton);
        tilePane.setOrientation(Orientation.VERTICAL);
        tilePane.setBackground(Background.fill(Color.MINTCREAM));

        //En testant, je me suis rendue compte qu'il n'y avait que la height qui faisait n'importe
        //quoi niveau taille d'origine mais la width non. Sauf que je sais pas prk la height n'est pas
        //bien seule contrairement à la width. Tu peux check ça stv ça nous évitera les 4 lignes de code below.
        tilePane.maxHeightProperty().bind(Bindings.createDoubleBinding(() ->
                        osmButton.getHeight() + cyclosmButton.getHeight() + label.getHeight()
                , osmButton.heightProperty(), cyclosmButton.heightProperty(),
                label.heightProperty()));

//        tilePane.maxWidthProperty().bind(Bindings.createDoubleBinding(() ->
//                        Math.max(Math.max(osmButton.getWidth(), cyclosmButton.getWidth()),
//                                label.getWidth())
//                , osmButton.widthProperty(), cyclosmButton.widthProperty()));


        //tilePane.setPickOnBounds(false); je pense c useless.

        Pane switchOsm = new Pane(tilePane);
        switchOsm.setPickOnBounds(false);

        tilePane.layoutYProperty().bind(Bindings.createDoubleBinding(
                () -> exporter(routeBean).getHeight(),  exporter(routeBean).heightProperty()
        ));
        tilePane.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> switchOsm.getWidth() - tilePane.getWidth(),  switchOsm.widthProperty(), tilePane.widthProperty()
        ));

        return switchOsm;
    }

}
