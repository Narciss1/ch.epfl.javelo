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

    private final ObjectProperty<MapViewParameters> mapProperty;
    private final ObjectProperty<TileManager> tileManagerP;
    private final WaypointsManager waypointsManager;
    private final Pane pane;
    private final Canvas canvas;
    private boolean redrawNeeded;

    /**
     * Number of pixels on the side of a tile
     */
    private final static int PIXELS_IN_TILE = 256;
    /**
     * Minimum value of zoom level
     */
    private final static int MIN_ZOOM_LEVEL = 8;
    /**
     * Maximum value of zoom level
     */
    private final static int MAX_ZOOM_LEVEL = 19;
    /**
     * A minimum scrolling time delta
     */
    private final static int MIN_SCROLL_TIME_DELTA = 200;

    /**
     * Constructor
     * @param tileManager a tile manager used to get the tiles from the map
     * @param waypointsManager a crossing points manager
     * @param mapProperty a JavaFX property containing the ch.epfl.javelo.parameters of the displayed map
     */
    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager,
                          ObjectProperty<MapViewParameters> mapProperty) {
        this.tileManagerP = new SimpleObjectProperty<>(tileManager);
        this.mapProperty = mapProperty;
        this.waypointsManager = waypointsManager;
        canvas = new Canvas();

        pane = new Pane(canvas);

        bindings();
        addListeners();
        baseMapEvents();
    }

    /**
     * Returns the JavaFX panel displaying the background map
     * @return a JavaFX panel displaying the background map
     */
    public Pane pane() {
        return pane;
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
            for (double x = 0; x <= xNumberTiles; ++x) {
                indexX = (int) (newXTopLeft / PIXELS_IN_TILE);
                indexY = (int) (yTopLeft / PIXELS_IN_TILE);
                if (TileManager.TileId.isValid(mapProperty.get().zoomLevel(),
                        indexX, indexY)) {
                    TileManager.TileId tileId = new TileManager.TileId(
                            mapProperty.get().zoomLevel(), indexX, indexY);
                    try {
                        Image image = tileManagerP.get().imageForTileAt(tileId);
                        canvasGraphicsContext.drawImage(image,
                                PIXELS_IN_TILE * indexX - mapProperty.get().xCoordinate(),
                                PIXELS_IN_TILE * indexY - mapProperty.get().yCoordinate());
                    } catch (IOException e) {
                    }
                    newXTopLeft += PIXELS_IN_TILE;
                }
            }
            yTopLeft += PIXELS_IN_TILE;
        }
    }

    /**
     * Redraws the map if and only if the attribute redrawNeeded is true
     */
    private void redrawIfNeeded(){
        if (!redrawNeeded) return;
        redrawNeeded = false;
        tilesDraw();
    }

    /**
     * Requests a redrawing on the next beat by forcing JavaFX to perform the next beat,
     * even if from its point of view this is not necessary
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    /**
     * Binds the elements in the pane to its properties for their placement
     */
    private void bindings () {
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
    }

    /**
     * Adds the listeners to the properties
     */
    private void addListeners() {
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
        tileManagerP.addListener(l -> redrawOnNextPulse());
    }

    /**
     * Manages the events related to the pane
     */
    private void baseMapEvents(){
        SimpleLongProperty minScrollTime = new SimpleLongProperty();
        ObjectProperty<Point2D> mousePositionProperty = new SimpleObjectProperty<>();

        pane.setOnScroll(e ->
                changeMapViewParametersAfterZoomByScrolling(minScrollTime, e));
        pane.setOnMousePressed(e ->
                mousePositionProperty.setValue(new Point2D(e.getX(), e.getY())));
        pane.setOnMouseDragged(e ->
                changeMapViewParametersAfterSlide(mousePositionProperty, e));
        pane.setOnMouseReleased(e -> {
            if (!e.isStillSincePress()) {
                changeMapViewParametersAfterSlide(mousePositionProperty, e);
            } else {
                Point2D mousePointer = new Point2D(e.getX(), e.getY());
                Point2D newTopLeft = mousePointer.add(mapProperty.get().topLeft());
                PointWebMercator point = PointWebMercator.of(mapProperty.get().zoomLevel(),
                        newTopLeft.getX(), newTopLeft.getY());
                waypointsManager.addWaypoint(point.x(), point.y());
            }
        });
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
        Point2D newTopLeft = mapProperty.get().topLeft().add(gap);
        mapProperty.setValue(mapProperty.get().withMinXY(
                    newTopLeft.getX(),
                    newTopLeft.getY()));
    }

    /**
     * Updates the mapViewParameters after a change in the zoom level of the map
     * @param minScrollTime a minimum scrolling time
     * @param e a scroll event
     */
    private void changeMapViewParametersAfterZoomByScrolling
    (SimpleLongProperty minScrollTime, ScrollEvent e) {
        if (e.getDeltaY() == 0d) return;
        long currentTime = System.currentTimeMillis();
        if (currentTime < minScrollTime.get()) return;
        minScrollTime.set(currentTime + MIN_SCROLL_TIME_DELTA);
        int zoomDelta = (int)Math.signum(e.getDeltaY());
        PointWebMercator pointUnderMouse =  mapProperty.get().
                pointAtPointWebMercator(e.getX(), e.getY());
        changeZoom(zoomDelta, pointUnderMouse);
    }

    /**
     * Changes the zoom
     * @param zoomDelta the difference of zoom between the current one and the next one
     * @param pointToZoomIn the point in the map that should appear in the same place
     * after the zooming
     */
    private void changeZoom(int zoomDelta, PointWebMercator pointToZoomIn) {
        int newZoom = Math2.clamp(MIN_ZOOM_LEVEL,
                zoomDelta + mapProperty.get().zoomLevel(), MAX_ZOOM_LEVEL);
        //This calculus is due to the fact that, since the point under the mouse does not
        //change after the zooming (or that the center point in the map stays in the center
        // after the zooming); therefore its distance to the old top left corner point
        //of the map regarding the old zoom level should be the same as its distance to the
        //new top left corner point regarding the new zoom level.
        Point2D zoomIn2D = new Point2D(
                pointToZoomIn.xAtZoomLevel(newZoom)
                - pointToZoomIn.xAtZoomLevel(mapProperty.get().zoomLevel()),
                pointToZoomIn.yAtZoomLevel(newZoom)
                - pointToZoomIn.yAtZoomLevel(mapProperty.get().zoomLevel()));
        Point2D topLeft = mapProperty.get().topLeft();
        Point2D newTopLeft = zoomIn2D.add(topLeft);
        mapProperty.setValue(new MapViewParameters (newZoom, newTopLeft.getX(), newTopLeft.getY()));
    }

    /**
     * Changes the value contained in tile manager property
     * @param tileManager a new tile manager
     */
    public void setTileManager(TileManager tileManager) {
        tileManagerP.set(tileManager);
    }

}