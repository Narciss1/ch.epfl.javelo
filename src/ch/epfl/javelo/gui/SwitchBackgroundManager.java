package ch.epfl.javelo.gui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Manages the display of the buttons allowing to switch the map background.
 * @author Lina Sadgal (342075)
 * @author Aya Hamane (345565)
 */
public final class SwitchBackgroundManager {

    private final AnnotatedMapManager annotatedMapManager;
    private final ElevationProfileManager elevationProfileManager;
    private final Path cacheBasePath;
    private final Pane pane;
    private final Label label;
    private final Label speedLabel;
    private final TextField averageSpeed;
    private final RadioButton osmButton;
    private final RadioButton cyclosmButton;
    private final ToggleGroup backgroundButtons;
    private final TilePane tilePane;
    private final TilePane speedPane;
    private final Consumer<String> errorConsumer;

    private final static String LABEL_TEXT = "Fond de carte :";
    private final static String LABEL_SPEED = "  Vitesse moyenne :";
    private final static String DEFAULT_AVERAGE_SPEED = "25";
    private final static String OPEN_STREET_MAP = "OpenStreetMap";
    private final static String OPEN_STREET_MAP_SERVER = "tile.openstreetmap.org";
    private final static String CYCLO_OSM = "CyclOSM           ";
    private final static String CYCLO_OSM_SERVER = "a.tile-cyclosm.openstreetmap.fr/cyclosm";
    private final static String ERROR_MESSAGE = "Entrée invalide";

    public SwitchBackgroundManager(AnnotatedMapManager annotatedMapManager,
                                   ElevationProfileManager elevationProfileManager, Path cacheBasePath,
                                   Consumer<String> errorConsumer) {
        this.annotatedMapManager = annotatedMapManager;
        this.elevationProfileManager = elevationProfileManager;
        this.cacheBasePath = cacheBasePath;
        this.errorConsumer = errorConsumer;

        label = new Label(LABEL_TEXT);
        osmButton = new RadioButton(OPEN_STREET_MAP);
        osmButton.setSelected(true);
        cyclosmButton = new RadioButton(CYCLO_OSM);

        backgroundButtons = new ToggleGroup();
        osmButton.setToggleGroup(backgroundButtons);
        cyclosmButton.setToggleGroup(backgroundButtons);

        tilePane = new TilePane(label, osmButton, cyclosmButton);
        tilePane.setOrientation(Orientation.VERTICAL);
        tilePane.setBackground(Background.fill(Color.MINTCREAM));
        tilePane.maxHeightProperty().bind(Bindings.createDoubleBinding(() ->
                        osmButton.getHeight() + cyclosmButton.getHeight() + label.getHeight()
                , osmButton.heightProperty(), cyclosmButton.heightProperty(),
                label.heightProperty()));


        speedLabel = new Label(LABEL_SPEED);
        speedLabel.setMaxWidth(150);
        averageSpeed = new TextField(DEFAULT_AVERAGE_SPEED);
        averageSpeed.setMaxWidth(105);
        averageSpeed.setAlignment(Pos.BASELINE_CENTER);

        speedPane = new TilePane(speedLabel, averageSpeed);
        speedPane.setOrientation(Orientation.HORIZONTAL);
        speedPane.setBackground(Background.fill(Color.MINTCREAM));
        speedPane.maxHeightProperty().bind(Bindings.createDoubleBinding(() ->
                        speedLabel.getHeight() + averageSpeed.getHeight(),
                speedLabel.heightProperty(), averageSpeed.heightProperty()));
        speedPane.maxWidthProperty().bind(Bindings.createDoubleBinding(() ->
                       tilePane.getWidth(), tilePane.widthProperty()));

        averageSpeed.textProperty().addListener((p, oldText, newText) -> {
            if(elevationProfileManager.elevationProfileProperty().get() != null) {
               /* boolean canCompute = true;
                byte[] characters = newText.getBytes();
                for(byte b : characters) {
                    if(b < 48 || b > 57) {
                        canCompute = false;
                    }
                }*/
                try {
                    averageSpeed.setOnAction(e -> elevationProfileManager.setStatsProperty(Double.valueOf(newText) / 60d));;
                } catch (NumberFormatException e) {
                    errorConsumer.accept(ERROR_MESSAGE);
                }
            }
        });

        pane = new Pane(tilePane, speedPane);
        pane.setPickOnBounds(false);

        bindings();
        events();
    }

    public Pane pane() {
        return pane;
    }

    private void events() {
        osmButton.setOnAction(e -> annotatedMapManager.setTileManager(
                new TileManager(cacheBasePath, OPEN_STREET_MAP_SERVER)));
        cyclosmButton.setOnAction(e -> annotatedMapManager.setTileManager(
                new TileManager(cacheBasePath, CYCLO_OSM_SERVER)));
    }

    private void bindings() {
        tilePane.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> pane.getWidth() - tilePane.getWidth(),
                pane.widthProperty(), tilePane.widthProperty()
        ));
        speedPane.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> pane.getWidth() - speedPane.getWidth(),
                pane.widthProperty(), speedPane.widthProperty()
        ));
        speedPane.layoutYProperty().bind(Bindings.createDoubleBinding(
                () -> tilePane.getHeight(),
                speedPane.heightProperty()
        ));
    }
}



