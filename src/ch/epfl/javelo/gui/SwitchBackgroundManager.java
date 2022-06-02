package ch.epfl.javelo.gui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import ch.epfl.javelo.parameters.Language;
import java.nio.file.Path;

/**
 * Manages the display of the buttons allowing to switch the map background.
 * @author Lina Sadgal (342075)
 * @author Aya Hamane (345565)
 */
public final class SwitchBackgroundManager {

    private final AnnotatedMapManager annotatedMapManager;
    private final Path cacheBasePath;
    private final Pane pane;
    private final Label label;

    private final RadioButton osmButton;
    private final RadioButton cyclosmButton;
    private final TilePane tilePane;

    private final static String DEFAULT_LABEL_TEXT = "Fond de carte :";
    private final static String OPEN_STREET_MAP = "OpenStreetMap";
    private final static String OPEN_STREET_MAP_SERVER = "tile.openstreetmap.org";
    private final static String CYCLO_OSM = "CyclOSM";
    private final static String CYCLO_OSM_SERVER = "a.tile-cyclosm.openstreetmap.fr/cyclosm";

    public SwitchBackgroundManager(AnnotatedMapManager annotatedMapManager, Path cacheBasePath) {
        this.annotatedMapManager = annotatedMapManager;
        this.cacheBasePath = cacheBasePath;

        label = new Label(DEFAULT_LABEL_TEXT);
        osmButton = new RadioButton(OPEN_STREET_MAP);
        osmButton.setSelected(true);
        cyclosmButton = new RadioButton(CYCLO_OSM);


        ToggleGroup backgroundButtons = new ToggleGroup();
        osmButton.setToggleGroup(backgroundButtons);
        cyclosmButton.setToggleGroup(backgroundButtons);

        tilePane = new TilePane(label, osmButton, cyclosmButton);
        tilePane.setOrientation(Orientation.VERTICAL);
        tilePane.setBackground(Background.fill(Color.MINTCREAM));


        //En testant, je me suis rendue compte qu'il n'y avait que la height qui faisait n'importe
        //quoi niveau taille d'origine mais la width non. Sauf que je sais pas prk la height n'est pas
        //bien seule contrairement à la width.
        tilePane.maxHeightProperty().bind(Bindings.createDoubleBinding(() ->
                        osmButton.getHeight() + cyclosmButton.getHeight() + label.getHeight()
                , osmButton.heightProperty(), cyclosmButton.heightProperty(),
                label.heightProperty()));

        pane = new Pane(tilePane);
        pane.setPickOnBounds(false);
        tilePane.setTileAlignment(Pos.CENTER_LEFT);
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
    }

    public void changeLanguage(Language language){
        label.setText(language.getBackgroundMessage());
    }

}



