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

    private final static int HIGHLIGHTED_POSITION_RADIUS = 5;

    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParametersProperty,
                        Consumer<String> errorConsumer) {
        this.routeBean = routeBean;
        this.mapProperty = mapViewParametersProperty;
        this.errorConsumer = errorConsumer;
        pane = new Pane();
        pane.setPickOnBounds(false);
    }

    public Pane pane() {
        return pane;
    }

    private void drawItinerary() {
        //Constante ??
        Polyline polylineItinerary = new Polyline();
        polylineItinerary.setId("route");
        pane.getChildren().add(polylineItinerary);
        if (routeBean.routeProperty().get() == null){
            polylineItinerary.setVisible(false);
            return;
        }
        List<PointCh> pointsItinerary = routeBean.routeProperty().get().points();
        List<Double> pointsCoordinates = new ArrayList<>();
        for (PointCh point : pointsItinerary) {
            pointsCoordinates.add(mapProperty.get().viewX(PointWebMercator.ofPointCh(point)));
            pointsCoordinates.add(mapProperty.get().viewY(PointWebMercator.ofPointCh(point)));

        }
        //C'est quoi son délire ??
        polylineItinerary.setLayoutX();
        polylineItinerary.setLayoutY();
        polylineItinerary.getPoints().addAll(pointsCoordinates);
    }

    private void drawHighligthedPosition() {
        Circle highlightedPosition = new Circle();
        pane.getChildren().add(highlightedPosition);
        double position = routeBean.highlightedPosition();
        if (isNaN(position)){
            highlightedPosition.setVisible(false);
            return;
        }
        PointWebMercator pointWebMercatorHighlightedPosition = PointWebMercator.ofPointCh(
                routeBean.routeProperty().get().pointAt(position));
        //setCenter / setlayout ?
        highlightedPosition.setCenterX(
                mapProperty.get().viewX(pointWebMercatorHighlightedPosition));
        highlightedPosition.setCenterY(mapProperty.get().viewY(pointWebMercatorHighlightedPosition));
        highlightedPosition.setRadius(HIGHLIGHTED_POSITION_RADIUS);
    }

}
