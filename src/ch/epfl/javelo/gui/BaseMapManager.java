package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;


import java.io.IOException;

public final class BaseMapManager {

    private final TileManager tileManager;
    private final MapViewParameters mapParameters;
    private final Pane pane;
    private final Canvas canvas;
    private boolean redrawNeeded;

    private final static int PIXELS_IN_TILE = 256;


    //Il faut un dernier paramètre
    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters> mapProperty) {
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
        //VOIR PIAZZA 1071
        if(redrawNeeded) {
            redrawOnNextPulse();
        }
    }

    public void tilesDraw(){
        GraphicsContext canvasGraphicsContext = canvas.getGraphicsContext2D();
        double xTopLeft = mapParameters.xCoordinate();
        double yTopLeft = mapParameters.yCoordinate();
        int indexXTopLeft = 0, indexYTopLeft = 0;

        for(double y = yTopLeft; y < yTopLeft + canvas.getHeight(); y += PIXELS_IN_TILE) {
            for(double x = xTopLeft; x < xTopLeft + canvas.getWidth(); x += PIXELS_IN_TILE) {
                indexXTopLeft = (int) Math.floor( x / PIXELS_IN_TILE);
                indexYTopLeft = (int) Math.floor( y / PIXELS_IN_TILE);
                TileManager.TileId tileId = new TileManager.TileId(mapParameters.zoomLevel(),
                        indexXTopLeft, indexYTopLeft);
                try {
                    Image image = tileManager.imageForTileAt(tileId);
                    PointWebMercator point = new PointWebMercator(x,y);
                    canvasGraphicsContext.drawImage(image, mapParameters.viewX(point),
                            mapParameters.viewY(point));
                    //PIAZZA DRAWIMAGE
                } catch (IOException e){
                   continue;
                }
            }
        }
    }

    private void redrawIfNeeded(){
        if (!redrawNeeded) return;
        redrawNeeded = false;
        tilesDraw();
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    public Pane pane() {
        return pane;
    }
}
