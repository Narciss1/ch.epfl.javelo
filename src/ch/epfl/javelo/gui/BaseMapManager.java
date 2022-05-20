package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.io.IOException;
import java.util.Collections;

/**
 * Manages the display and interaction with the background map
 * @author Hamane Aya (345565)
 * @author Sadgal Lina (342075)
 */
public final class BaseMapManager {

    private final TileManager tileManager;
    private final ObjectProperty<MapViewParameters> mapProperty;
    private final Pane pane;
    private final Canvas canvas;
    private final Button reverseItineraryButton;
    private final Button removePointsButton;
    private boolean redrawNeeded;
    private final WaypointsManager waypointsManager;

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
        canvas = new Canvas();

        reverseItineraryButton = new Button();
        SVGPath reverseIcon2 = new SVGPath();
        reverseIcon2.setContent("M5.79,9.71A1,1,0,1,0,7.21,8.29L5.91,7h12A1.56,1.56" +
                ",0,0,1,19.5,8.53V11a1,1,0,0,0,2,0V8.53A3.56,3.56,0,0,0,17.91,5h-12l1.3-1.29a1" +
                ",1,0,0,0,0-1.42,1,1,0,0,0-1.42,0l-3,3a1,1,0,0,0,0,1.42Z");
        SVGPath reverseIcon1 = new SVGPath();
        reverseIcon1.setContent("M6.09,19h12l-1.3,1.29a1,1,0,0,0,1.42,1.42l3-3a1," +
                "1,0,0,0,0-1.42l-3-3a1,1,0,0,0-1.42,0,1,1,0,0,0,0,1.42L18.09,17h-12A1.56," +
                "1.56,0,0,1,4.5,15.47V13a1,1,0,0,0-2,0v2.47A3.56,3.56,0,0,0,6.09,19Z");
        Group reverseIcon = new Group(reverseIcon1, reverseIcon2);
        reverseItineraryButton.setGraphic(reverseIcon);

        removePointsButton = new Button();
        SVGPath removeIcon2 = new SVGPath();
        removeIcon2.setContent("M6.8,8.8h11L17,22.6 H7.6L6.8,8.8z M4.9,7l1,17.4h12.8 l1-17.4 H4.9z");
        SVGPath removeIcon1 = new SVGPath();
        removeIcon1.setContent("M20.4,4h-4.8l-0.5-1.6 H9.5L9,4 H4.2 L3.5,8.6h17.6 L20.4,4z M9.9,3.2h4.8 L14.9,3.9h-5.2z M5.6,6.7l0.2-1 h13l0.2,1 H5.6z");
        Group removeIcon = new Group(removeIcon1, removeIcon2);
        removePointsButton.setGraphic(removeIcon);

        pane = new Pane(canvas, reverseItineraryButton, removePointsButton);

        removePointsButton.layoutYProperty().bind(Bindings.createDoubleBinding( () ->
                pane.getHeight() - reverseItineraryButton.getHeight(),
                pane.heightProperty()));
        reverseItineraryButton.layoutYProperty().bind(Bindings.createDoubleBinding( () ->
                pane.getHeight() - reverseItineraryButton.getHeight() - removePointsButton.getHeight(),
                pane.heightProperty()));

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        baseMapEvents();
        canvas.widthProperty().addListener((p, oldM, newM) ->
                redrawOnNextPulse());
        canvas.heightProperty().addListener((p, oldM, newM) ->
                redrawOnNextPulse());
        mapProperty.addListener((p, oldM, newM) ->
                redrawOnNextPulse());
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
    }

    /**
     * Generates and draws each of the visible tiles at least partially
     * If an exception is thrown by the tile manager (of type IOException),
     * the corresponding tile is simply not drawn
     * @throws IllegalArgumentException if the index X or the index Y of the tile are not valid
     */
    public void tilesDraw(){
        GraphicsContext canvasGraphicsContext = canvas.getGraphicsContext2D();
        canvasGraphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        double xTopLeft = mapProperty.get().xCoordinate();
        double yTopLeft = mapProperty.get().yCoordinate();
        int xNumberTiles = ((int) canvas.getWidth() / PIXELS_IN_TILE) + 1;
        int yNumberTiles = ((int) canvas.getHeight() / PIXELS_IN_TILE) + 1;
        int indexX, indexY;
        for(double y = 0; y <= yNumberTiles; ++y) {
            double newXTopLeft = xTopLeft;
            for(double x = 0; x <= xNumberTiles; ++x) {
                indexX = (int) (newXTopLeft / PIXELS_IN_TILE);
                indexY = (int) (yTopLeft / PIXELS_IN_TILE);
                TileManager.TileId tileId = new TileManager.TileId(mapProperty.get().zoomLevel(),
                        indexX, indexY);
                try {
                    Image image = tileManager.imageForTileAt(tileId);
                    canvasGraphicsContext.drawImage(image,
                            PIXELS_IN_TILE * indexX - mapProperty.get().xCoordinate(),
                            PIXELS_IN_TILE * indexY - mapProperty.get().yCoordinate());
                } catch (IOException e) {}
                newXTopLeft += PIXELS_IN_TILE;
            }
            yTopLeft += PIXELS_IN_TILE;
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
     * Manages the events related to the pane
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

        reverseItineraryButton.setOnAction(e -> waypointsManager.reverseItinerary());
        removePointsButton.setOnAction(e -> waypointsManager.removeItinerary());
    }

    /**
     * Updates the mapViewParameters after a shift of the map
     * @param mousePositionProperty a property containing the position of the map
     * @param e a mouse event
     */
    private void changeMapViewParametersAfterSlide
        (ObjectProperty<Point2D> mousePositionProperty, MouseEvent e) {
        Point2D oldMousePosition = mousePositionProperty.get();
        mousePositionProperty.setValue(new Point2D(e.getX(), e.getY()));
        Point2D gap = oldMousePosition.subtract(mousePositionProperty.get());
        mapProperty.setValue(mapProperty.get().withMinXY(
                mapProperty.get().xCoordinate() + gap.getX(),
                mapProperty.get().yCoordinate() + gap.getY()));
    }

    /**
     * Updates the mapViewParameters after a change in the zoom level of the map
     * @param minScrollTime a minimum scrolling time
     * @param e a scroll event
     */
    private void changeMapViewParametersAfterZoom
    (SimpleLongProperty minScrollTime, ScrollEvent e) {
        if (e.getDeltaY() == 0d) return;
        long currentTime = System.currentTimeMillis();
        if (currentTime < minScrollTime.get()) return;
        minScrollTime.set(currentTime + 200);
        int zoomDelta = (int)Math.signum(e.getDeltaY());
        int newZoom = Math2.clamp(8, zoomDelta + mapProperty.get().zoomLevel(), 19);
        PointWebMercator pointUnderMouse =  mapProperty.get().pointAt(e.getX(), e.getY());
        //This calculus is due to the fact that, since the point under the mouse does not
        //change after the zooming; therefore its distance to the old top left corner point
        //of the map regarding the old zoom level should be the same as its distance to the
        //new top left corner point  regarding the new zoom level.
        double newTopLeftX = pointUnderMouse.xAtZoomLevel(newZoom)
                - pointUnderMouse.xAtZoomLevel(mapProperty.get().zoomLevel())
                +  mapProperty.get().xCoordinate();
        double newTopLeftY = pointUnderMouse.yAtZoomLevel(newZoom)
                - pointUnderMouse.yAtZoomLevel(mapProperty.get().zoomLevel())
                +  mapProperty.get().yCoordinate();
        mapProperty.setValue(new MapViewParameters (newZoom, newTopLeftX, newTopLeftY));
    }
}