package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.io.IOException;

public final class BaseMapManager {

    private final TileManager tileManager;
    private final MapViewParameters mapParameters;
    private final Pane pane;
    private final Canvas canvas;
    private boolean redrawNeeded;

    private final static int PIXELS_IN_TILE = 256;


    public BaseMapManager(TileManager tileManager, ObjectProperty<MapViewParameters> mapProperty) throws IOException {
        this.tileManager = tileManager;
        this.mapParameters = mapProperty.get();
        pane = new Pane();
        canvas = new Canvas();
        pane.getChildren().add(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
           // newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        }); //VOIR PIAZZA 1071
        if(redrawNeeded) {
            redrawOnNextPulse();
        }
    }

    public void tilesDraw() throws IOException {
        GraphicsContext canvasGraphicsContext = canvas.getGraphicsContext2D();
        double xTopLeft = mapParameters.xCoordinate();
        double yTopLeft = mapParameters.yCoordinate();
        int indexXTopLeft = 0, indexYTopLeft = 0;
        double imageWidth = 0, imageHeight = 0;

        for(double y = yTopLeft; y < yTopLeft + canvas.getHeight(); y += PIXELS_IN_TILE) {
            for(double x = xTopLeft; x < xTopLeft + canvas.getWidth(); x += PIXELS_IN_TILE) {
                indexXTopLeft = (int) Math.floor( x / PIXELS_IN_TILE);
                indexYTopLeft = (int) Math.floor( y / PIXELS_IN_TILE);
                TileManager.TileId tileId = new TileManager.TileId(mapParameters.zoomLevel(),
                        indexXTopLeft, indexYTopLeft);
                try {
                    Image image = tileManager.imageForTileAt(tileId);
                    canvasGraphicsContext.drawImage(image, imageWidth, imageHeight); //PIAZZA DRAWIMAGE
                    imageWidth += image.getWidth();
                    if(x + PIXELS_IN_TILE >= xTopLeft + canvas.getWidth()) { // A AMELIORER
                        imageHeight += image.getHeight();
                    }
                } catch (IOException e){
                    continue;
                }
            }
        }
    }

    private void redrawIfNeeded() throws IOException {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        tilesDraw(); //A VERIFIER
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    public Pane pane() {
        return pane;
    }
}
