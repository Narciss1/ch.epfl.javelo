package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import java.io.IOException;

/**
 * Manages the display and interaction with the background map
 * @author Hamane Aya (345565)
 * @author Sadgal Lina (342075)
 */
public final class BaseMapManager {

    private final TileManager tileManager;
    private final MapViewParameters mapParameters;
    private final Pane pane;
    private final Canvas canvas;
    private boolean redrawNeeded;

    /**
     *Number of pixels on the side of a tile
     */
    private final static int PIXELS_IN_TILE = 256;

    /**
     * Constructor
     * @param tileManager a tile manager used to get the tiles from the map
     * @param waypointsManager a crossing points manager
     * @param mapProperty a JavaFX property containing the parameters of the displayed map
     */
    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager,
                          ObjectProperty<MapViewParameters> mapProperty) {
        this.tileManager = tileManager;
        this.mapParameters = mapProperty.get();
        pane = new Pane();
        canvas = new Canvas();
        pane.getChildren().add(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        redrawOnNextPulse();
    }

    /**
     * Generates and draws each of the visible tiles at least partially
     * If an exception is thrown by the tile manager (of type IOException),
     * the corresponding tile is simply left undrawn
     * @throws IllegalArgumentException if the index X or the index Y of the tile are not valid
     */
    public void tilesDraw(){
        GraphicsContext canvasGraphicsContext = canvas.getGraphicsContext2D();
        double xTopLeft = mapParameters.xCoordinate();
        double yTopLeft = mapParameters.yCoordinate();
        int indexX, indexY;
        for(double y = yTopLeft; y < yTopLeft + canvas.getHeight() + PIXELS_IN_TILE; y += PIXELS_IN_TILE) {
            for(double x = xTopLeft; x < xTopLeft + canvas.getWidth() + PIXELS_IN_TILE; x += PIXELS_IN_TILE) {
                indexX = (int) Math.floor( x / PIXELS_IN_TILE);
                indexY = (int) Math.floor( y / PIXELS_IN_TILE);
                TileManager.TileId tileId = new TileManager.TileId(mapParameters.zoomLevel(),
                        indexX, indexY);
                try {
                    Image image = tileManager.imageForTileAt(tileId);
                    canvasGraphicsContext.drawImage(image, 256 * indexX - mapParameters.xCoordinate(),
                             (256 * indexY - mapParameters.yCoordinate()));
                } catch (IOException e){
                   continue;
                }
            }
        }
    }

    /**
     * Returns the JavaFX panel displaying the background map
     * @return a JavaFX panel displaying the background map
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Redraw the map if and only if the attribute redrawNeeded is true
     */
    private void redrawIfNeeded(){
        if (!redrawNeeded) return;
        redrawNeeded = false;
        tilesDraw();
    }

    /**
     * Requests a redraw on the next beat by forcing JavaFX to perform the next beat,
     * even if from its point of view this is not necessary
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
