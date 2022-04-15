package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.function.Consumer;

public final class WaypointsManager {

    private final Pane pane;
    private final Graph graph;
    private final MapViewParameters mapViewParameters;
    private final ObservableList<Waypoint> wayPoints;


    //Il faut un dernier paramètre mais c'est dans le truc de mardi
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapProperty,
                            ObservableList<Waypoint> wayPoints, Consumer<String> errorConsumer) {
        pane = new Pane();
        pane.setPrefHeight(300);
        pane.setPrefWidth(600);
        this.graph = graph;
        this.mapViewParameters = mapProperty.get();
        this.wayPoints = wayPoints;
        //Not sure
        pane.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::addSVGPaths);
        });
    }

    public Pane pane() {
        return pane;
    }

    public void addWaypoint(double x, double y) {
        double e = Ch1903.e(WebMercator.lon(x), WebMercator.lat(y));
        double n = Ch1903.n(WebMercator.lon(x), WebMercator.lat(y));
        PointCh pointCh = new PointCh(e, n);
        wayPoints.add(new Waypoint(pointCh, graph.nodeClosestTo(pointCh, 500)));
    }

    private void addSVGPaths() {

        int wayPointCounter = 0;

        for (Waypoint wayPoint : wayPoints) {
            Group group = new Group();
            group.getStyleClass().add("pin");
            SVGPath exterior = new SVGPath();
            SVGPath interior = new SVGPath();
            exterior.getStyleClass().add("pin_outside");
            interior.getStyleClass().add("pin_inside");
            exterior.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
            interior.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
            if (wayPointCounter == 0) {
                group.getStyleClass().add("first");
            }
            else if (wayPointCounter == wayPoints.size() - 1) {
                group.getStyleClass().add("last");
            } else {
                group.getStyleClass().add("middle");
            }
            group.getChildren().add(exterior);
            group.getChildren().add(interior);
            group.setLayoutX(mapViewParameters.viewX(PointWebMercator.ofPointCh(
                    new PointCh(wayPoint.pointCh().e(), wayPoint.pointCh().n()))));
            group.setLayoutY(mapViewParameters.viewY(PointWebMercator.ofPointCh(
                    new PointCh(wayPoint.pointCh().e(), wayPoint.pointCh().n()))));
            pane.getChildren().add(group);
        }
    }

}
