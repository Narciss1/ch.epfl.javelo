package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
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

    public void tilesDraw(){
        //System.out.println("Je suis dans tilesDraw()");
        //System.out.println("pane height: " + pane.getHeight());
        //System.out.println("pane width: " + pane.getWidth());
        //System.out.println("canvas height: " + canvas.getHeight());
        //System.out.println("canvas width: " + canvas.getWidth());
        GraphicsContext canvasGraphicsContext = canvas.getGraphicsContext2D();
        double xTopLeft = mapParameters.xCoordinate();
        double yTopLeft = mapParameters.yCoordinate();
        int indexX = 0, indexY = 0;
        System.out.println("Width canvas : " + canvas.getWidth());
        System.out.println("Height canvas : " + canvas.getHeight());
        for(double y = yTopLeft; y < yTopLeft + canvas.getHeight() + PIXELS_IN_TILE; y += PIXELS_IN_TILE) {
            for(double x = xTopLeft; x < xTopLeft + canvas.getWidth() + PIXELS_IN_TILE; x += PIXELS_IN_TILE) {
                indexX = (int) Math.floor( x / PIXELS_IN_TILE);
                indexY = (int) Math.floor( y / PIXELS_IN_TILE);
                System.out.println("indice X : " + indexX);
                System.out.println("indice Y : " + indexY);
                TileManager.TileId tileId = new TileManager.TileId(mapParameters.zoomLevel(),
                        indexX, indexY);
                try {
                    Image image = tileManager.imageForTileAt(tileId);
                    //System.out.println("x: " + x + " y: " + y);
                    //PointWebMercator point = PointWebMercator.of(mapParameters.zoomLevel(), x, y);
                    //System.out.println("x to drawImage : "  + (256 * indexX - mapParameters.xCoordinate()));
                    //System.out.println("y to drawImage : " + ((256 * (indexY + 1) - 1)- mapParameters.yCoordinate()));
                    canvasGraphicsContext.drawImage(image, 256 * indexX - mapParameters.xCoordinate(),
                             (256 * indexY - mapParameters.yCoordinate()));
                    //System.out.println("x: " + (x - mapParameters.xCoordinate()));
                    //System.out.println("y: " + (y - mapParameters.yCoordinate()));
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
