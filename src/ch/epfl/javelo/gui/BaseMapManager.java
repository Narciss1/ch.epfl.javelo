package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Manages the display and interaction with the background map
 * @author Hamane Aya (345565)
 * @author Sadgal Lina (342075)
 */
public final class BaseMapManager {

    private final TileManager tileManager;
    private ObjectProperty<MapViewParameters> mapProperty;
    private final Pane pane;
    private final Canvas canvas;
    private boolean redrawNeeded;
    private WaypointsManager waypointsManager;

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
        this.mapProperty = mapProperty;
        this.waypointsManager = waypointsManager;
        pane = new Pane();
        canvas = new Canvas();
        pane.getChildren().add(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        baseMapEvents();
        mapProperty.addListener((p, oldM, newM) ->
                redrawOnNextPulse());
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        redrawOnNextPulse();
    }

    /**
     * Generates and draws each of the visible tiles at least partially
     * If an exception is thrown by the tile manager (of type IOException),
     * the corresponding tile is simply not drawn
     * @throws IllegalArgumentException if the index X or the index Y of the tile are not valid
     */
    public void tilesDraw(){
        GraphicsContext canvasGraphicsContext = canvas.getGraphicsContext2D();
        double xTopLeft = mapProperty.get().xCoordinate();
        double yTopLeft = mapProperty.get().yCoordinate();
        int indexX, indexY;
        for(double y = yTopLeft; y < yTopLeft + canvas.getHeight() + PIXELS_IN_TILE; y += PIXELS_IN_TILE) {
            for(double x = xTopLeft; x < xTopLeft + canvas.getWidth() + PIXELS_IN_TILE; x += PIXELS_IN_TILE) {
                indexX = (int) Math.floor( x / PIXELS_IN_TILE);
                indexY = (int) Math.floor( y / PIXELS_IN_TILE);
                TileManager.TileId tileId = new TileManager.TileId(mapProperty.get().zoomLevel(),
                        indexX, indexY);
                try {
                    Image image = tileManager.imageForTileAt(tileId);
                    canvasGraphicsContext.drawImage(image, PIXELS_IN_TILE * indexX - mapProperty.get().xCoordinate(),
                            (PIXELS_IN_TILE * indexY - mapProperty.get().yCoordinate()));
                } catch (IOException e){
                    //What should we put here ? (To me we should leave it empty...
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

    /**
     *
     */
    private void baseMapEvents(){
        SimpleLongProperty minScrollTime = new SimpleLongProperty();
        ObjectProperty<Point2D> mousePositionProperty = new SimpleObjectProperty<>();

        pane.setOnScroll(e ->
                changeMapViewParametersAfterZoom(minScrollTime, e));

        pane.setOnMousePressed(e ->
                mousePositionProperty.setValue(new Point2D(e.getX(), e.getY())));

        pane.setOnMouseDragged(e ->
                changeMapViewParametersAfterSlide(mousePositionProperty, e));

        pane.setOnMouseReleased(e -> {
            if (!e.isStillSincePress()) {
                changeMapViewParametersAfterSlide(mousePositionProperty, e);
            } else {
                double x = e.getX() + mapProperty.get().xCoordinate();
                double y = e.getY() + mapProperty.get().yCoordinate();
                PointWebMercator point = PointWebMercator.of(mapProperty.get().zoomLevel(), x, y);
                waypointsManager.addWaypoint(point.x(), point.y());
            }
        });
    }

    /**
     *
     * @param mousePositionProperty
     * @param e
     */
    private void changeMapViewParametersAfterSlide(ObjectProperty<Point2D> mousePositionProperty, MouseEvent e){
        Point2D oldMousePosition = mousePositionProperty.get();
        mousePositionProperty.setValue(new Point2D(e.getX(), e.getY()));
        Point2D oldTopLeftPosition = oldMousePosition.subtract(mousePositionProperty.get());
        mapProperty.setValue(mapProperty.get().withMinXY
                (oldTopLeftPosition.getX() + mapProperty.get().xCoordinate(),
                        oldTopLeftPosition.getY() + mapProperty.get().yCoordinate()));
    }

    /**
     *
     * @param minScrollTime
     * @param e
     */
    private void changeMapViewParametersAfterZoom(SimpleLongProperty minScrollTime, ScrollEvent e){
        long currentTime = System.currentTimeMillis();
        if (currentTime < minScrollTime.get()) return;
        minScrollTime.set(currentTime + 250);
        double zoomDelta = Math.signum(e.getDeltaY());
        int newZoom = Math2.clamp(8, (int)zoomDelta + mapProperty.get().zoomLevel(), 19);
        double newX = mapProperty.get()
                .pointAt(e.getX(), e.getY())
                .xAtZoomLevel(newZoom)
                - mapProperty.get()
                .pointAt(e.getX(), e.getY())
                .xAtZoomLevel(mapProperty.get().zoomLevel())
                +  mapProperty.get()
                .xCoordinate();
        double newY = mapProperty.get()
                .pointAt(e.getX(), e.getY())
                .yAtZoomLevel(newZoom)
                - mapProperty.get()
                .pointAt(e.getX(), e.getY())
                .yAtZoomLevel(mapProperty.get().zoomLevel())
                +  mapProperty.get()
                .yCoordinate();
        mapProperty.setValue(new MapViewParameters (newZoom,
                newX, newY));
    }
}