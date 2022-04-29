package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.Double.isNaN;

public final class RouteManager {

    private RouteBean routeBean;
    private ReadOnlyObjectProperty<MapViewParameters> mapProperty;
    private Consumer<String> errorConsumer;
    private Pane pane;
    private Polyline polylineItinerary;

    private final static int HIGHLIGHTED_POSITION_RADIUS = 5;

    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParametersProperty,
                        Consumer<String> errorConsumer) {
        this.routeBean = routeBean;
        this.mapProperty = mapViewParametersProperty;
        this.errorConsumer = errorConsumer;
        pane = new Pane();
        polylineItinerary = new Polyline();
        pane.setPickOnBounds(false);
        createPolyline();
        createCircle();
        mapProperty.addListener((p, oldM, newM) -> {
                if (oldM.zoomLevel() == newM.zoomLevel()) {
                    System.out.println("che7");
                    moveItinerary();
                } else {
                    createPolyline();
                }
                createCircle();
                });
    }

    public Pane pane() {
        return pane;
    }

    private void createPolyline() {
        pane.getChildren().clear();
        polylineItinerary.getPoints().clear();
        polylineItinerary.setId("route");
        //pane.getChildren().add(polylineItinerary);
        if (routeBean.routeProperty().get() == null){
            polylineItinerary.setVisible(false);
            return;
        }
        List<PointCh> pointsItinerary = routeBean.routeProperty().get().points();
        PointWebMercator pointOrigin = PointWebMercator.ofPointCh(pointsItinerary.get(0));
        List<Double> pointsCoordinates = new ArrayList<>();
        for (PointCh point : pointsItinerary) {
            PointWebMercator pointInMercator = PointWebMercator.ofPointCh(point);
//            pointsCoordinates.add(mapProperty.get().viewX(pointInMercator) -
//                    mapProperty.get().viewX(pointOrigin));
//            pointsCoordinates.add(mapProperty.get().viewY(pointInMercator)
//            - mapProperty.get().viewY(pointOrigin));
            pointsCoordinates.add(PointWebMercator.ofPointCh(point).xAtZoomLevel(mapProperty.get().zoomLevel()));
            pointsCoordinates.add(PointWebMercator.ofPointCh(point).yAtZoomLevel(mapProperty.get().zoomLevel()));
        }
        polylineItinerary.getPoints().addAll(pointsCoordinates);
        moveItinerary();
    }

    private void moveItinerary() {
        pane.getChildren().clear();
        PointWebMercator point = PointWebMercator.ofPointCh(routeBean.routeProperty().get().points().get(0));
//        polylineItinerary.setLayoutX(mapProperty.get().viewX(point));
//        polylineItinerary.setLayoutY(mapProperty.get().viewY(point));
        polylineItinerary.setLayoutX(- mapProperty.get().xCoordinate());
        polylineItinerary.setLayoutY(- mapProperty.get().yCoordinate());
        pane.getChildren().add(polylineItinerary);
    }

    private void createCircle() {
        if(routeBean.routeProperty().get() == null) return;
        Circle highlightedPosition = new Circle();
        highlightedPosition.setId("highlight");
        pane.getChildren().add(highlightedPosition);
        double position = routeBean.highlightedPosition();
        if (isNaN(position)){
            highlightedPosition.setVisible(false);
            return;
        }
        PointWebMercator pointWebMercatorHighlightedPosition = PointWebMercator.ofPointCh(
                routeBean.routeProperty().get().pointAt(position));
        highlightedPosition.setCenterX(pointWebMercatorHighlightedPosition.x());
        highlightedPosition.setCenterY(pointWebMercatorHighlightedPosition.y());
        highlightedPosition.setRadius(HIGHLIGHTED_POSITION_RADIUS);
        highlightedPosition.setLayoutX(mapProperty.get().viewX(pointWebMercatorHighlightedPosition));
        highlightedPosition.setLayoutY(mapProperty.get().viewY(pointWebMercatorHighlightedPosition));
    }
}
