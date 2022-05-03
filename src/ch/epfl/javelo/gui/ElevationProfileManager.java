package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;

public final class ElevationProfileManager {

    private ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty; //null
    private ReadOnlyDoubleProperty highlightedPositionProperty; //nan
    private BorderPane borderPane;
    private Pane pane;
    private double xUnit;
    private double yUnit;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ReadOnlyDoubleProperty highlightedPositionProperty){
        this.elevationProfileProperty = elevationProfileProperty;
        this.highlightedPositionProperty = highlightedPositionProperty;
        borderPane = new BorderPane();
        pane = new Pane();
        System.out.println(pane.widthProperty().get());
        borderPane.getStylesheets().add("elevation_profile.css");
        pane.getStylesheets().add("elevation_profile.css");
        borderPane.getChildren().add(pane);
        changeUnits();
        createPolygone();
        pane.widthProperty().addListener(l -> {
                changeUnits();
                createPolygone();});
        pane.heightProperty().addListener(l -> {
                changeUnits();
                createPolygone();});
    }

    //remettre BorderPane
    public Pane pane() {
        return pane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty(){
        //ToDo
        return null;
    }

    private void changeUnits() {
        xUnit = elevationProfileProperty.get().length() / pane.getWidth();
        System.out.println("elevationProfileLength: " + elevationProfileProperty.get().length());
        System.out.println("width: " + pane.getWidth());
        yUnit = elevationProfileProperty.get().maxElevation() / pane.getHeight();
        System.out.println("elevationProfileMax: " + elevationProfileProperty.get().maxElevation());
        System.out.println("height: " + pane.getHeight());
        System.out.println(yUnit);
    }

    private void createPolygone() {
        List<Double> listPoints = new ArrayList<>();
        double position = 0;
        double elevation;
        listPoints.add(0.0);
        listPoints.add(0.0);
        while (position <= elevationProfileProperty.get().length()){
            elevation = elevationProfileProperty.get().elevationAt(position) / yUnit ;
            //System.out.println(position);
            listPoints. add(position / xUnit);
            listPoints. add(elevation);
            position += xUnit;
        }
        listPoints.add(position - xUnit);
        listPoints.add(0.0);
        System.out.println(listPoints);
        Polygon profile = new Polygon();
        profile.setId("profile");
        profile.getPoints().addAll(listPoints);
        pane.getChildren().clear();
        pane.getChildren().add(profile);
    }
}
