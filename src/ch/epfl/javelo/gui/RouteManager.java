package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
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
    private final Polyline polylineItinerary;
    private Circle circle;

    private final static int HIGHLIGHTED_POSITION_RADIUS = 5;

    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParametersProperty,
                        Consumer<String> errorConsumer) {
        this.routeBean = routeBean;
        this.mapProperty = mapViewParametersProperty;
        this.errorConsumer = errorConsumer;
        pane = new Pane();
        polylineItinerary = new Polyline();
        circle = new Circle();
        pane.setPickOnBounds(false);
        createPolyline();
        createCircle();
        mapProperty.addListener((p, oldM, newM) -> {
                if (oldM.zoomLevel() == newM.zoomLevel()) {
                    moveItinerary();
                } else {
                    createPolyline();
                }
                createCircle();
        });
        routeBean.routeProperty().addListener((InvalidationListener) l -> {
            createPolyline();
            createCircle();
        });
        routeBean.highlightedPositionProperty().addListener((InvalidationListener) l -> {
            createCircle();
            createPolyline();
        });
        routeEvents();
    }

    public Pane pane() {
        return pane;
    }

    private void createPolyline() {
        pane.getChildren().clear();
        polylineItinerary.getPoints().clear();
        polylineItinerary.setId("route");
        pane.getChildren().add(polylineItinerary);
        //Opérateur ternaire à conseiller ?
        if (routeBean.routeProperty().get() == null){
            polylineItinerary.setVisible(false);
            return;
        } else {
            polylineItinerary.setVisible(true);
        }
        List<PointCh> pointsItinerary = routeBean.routeProperty().get().points();
        List<Double> pointsCoordinates = new ArrayList<>();
        for (PointCh point : pointsItinerary) {
            PointWebMercator pointMercator = PointWebMercator.ofPointCh(point);
            pointsCoordinates.add(pointMercator.xAtZoomLevel(mapProperty.get().zoomLevel()));
            pointsCoordinates.add(pointMercator.yAtZoomLevel(mapProperty.get().zoomLevel()));
        }
        polylineItinerary.getPoints().addAll(pointsCoordinates);
        moveItinerary();
    }

    private void moveItinerary() {
        pane.getChildren().clear();
        polylineItinerary.setLayoutX(-mapProperty.get().xCoordinate());
        polylineItinerary.setLayoutY(-mapProperty.get().yCoordinate());
        pane.getChildren().add(polylineItinerary);
    }

    private void createCircle() {
        if(routeBean.routeProperty().get() == null) return;
        circle.setId("highlight");
        pane.getChildren().add(circle);
        double position = routeBean.highlightedPosition();
        if (isNaN(position)){
            circle.setVisible(false);
            return;
        }
        PointWebMercator pointWebMercatorHighlightedPosition = PointWebMercator.ofPointCh(
                routeBean.routeProperty().get().pointAt(position));
        //do we rly need all those center machin etc ?
        circle.setCenterX(pointWebMercatorHighlightedPosition.x());
        circle.setCenterY(pointWebMercatorHighlightedPosition.y());
        circle.setRadius(HIGHLIGHTED_POSITION_RADIUS);
        circle.setLayoutX(mapProperty.get().viewX(pointWebMercatorHighlightedPosition));
        circle.setLayoutY(mapProperty.get().viewY(pointWebMercatorHighlightedPosition));
    }

    public void routeEvents(){
        circle.setOnMouseClicked(e -> {
            if(routeBean.routeProperty().get() != null && !isNaN(routeBean.highlightedPosition())) {
                Point2D position = circle.localToParent(new Point2D(e.getX(), e.getY()));
                PointWebMercator pointMercator = mapProperty.get().pointAt(position.getX(), position.getY());
                PointCh pointCh = pointMercator.toPointCh();
                int closestNode = routeBean.routeProperty().get().nodeClosestTo(routeBean.highlightedPosition());
                Waypoint wayPoint = new Waypoint(pointCh, closestNode);
                //J'ai pas trop l'impression que c'est legit de jouer sur les références pour modifier
                //la liste de RouteBean là.
                ObservableList<Waypoint> newList = routeBean.waypoints();
                int index = routeBean.routeProperty().get().indexOfSegmentAt(routeBean.highlightedPosition()) + 1;
                boolean canAdd = true;
                int count = 0;
                while(canAdd && count < newList.size()){
                    if(newList.get(count).closestNodeId() == closestNode) {
                        canAdd = false;
                    }
                    ++count;
                }
                if(canAdd && !newList.contains(wayPoint)) {
                    newList.add(index, wayPoint);
                } else {
                    errorConsumer.accept("Un point de passage est déjà présent à cet endroit !");
                }
            }
        });
    }
}
