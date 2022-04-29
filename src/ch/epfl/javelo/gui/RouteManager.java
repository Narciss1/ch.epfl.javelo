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
    }

    public Pane pane() {
        return pane;
    }

    private void createPolyline() {
        polylineItinerary.setId("route");
        pane.getChildren().add(polylineItinerary);
        if (routeBean.routeProperty().get() == null){
            polylineItinerary.setVisible(false);
            System.out.println("elles sont invisibles ^^ hehe");
            return;
        }
        List<PointCh> pointsItinerary = routeBean.routeProperty().get().points();
        List<Double> pointsCoordinates = new ArrayList<>();
        for (PointCh point : pointsItinerary) {
            //pointsCoordinates.add(PointWebMercator.ofPointCh(point).xAtZoomLevel(mapProperty.get().zoomLevel()));
            //pointsCoordinates.add(PointWebMercator.ofPointCh(point).yAtZoomLevel(mapProperty.get().zoomLevel()));
            pointsCoordinates.add(PointWebMercator.ofPointCh(point).x());
            pointsCoordinates.add(PointWebMercator.ofPointCh(point).y());
        }
        System.out.println("size: "+ pointsCoordinates.size());
        polylineItinerary.getPoints().addAll(pointsCoordinates);
        movePolyline(); //à revoir en testant
    }

    private void movePolyline() {
        PointCh point = routeBean.routeProperty().get().points().get(0);
        polylineItinerary.setLayoutX(mapProperty.get().viewX(PointWebMercator.ofPointCh(point)));
        polylineItinerary.setLayoutY(mapProperty.get().viewY(PointWebMercator.ofPointCh(point)));
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
