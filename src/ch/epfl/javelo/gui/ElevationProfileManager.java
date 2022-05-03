package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;

public final class ElevationProfileManager {

    private ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty; //null
    private ReadOnlyDoubleProperty highlightedPositionProperty; //nan
    private BorderPane borderPane;
    private Pane pane;
    private ObjectProperty<Rectangle2D> rectangleProfile;
    private Insets rectangleInsets;
    private ObjectProperty<Transform> worldToScreen;
    private ObjectProperty<Transform> screenToWorld;
    private double xUnit;
    private double yUnit;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ReadOnlyDoubleProperty highlightedPositionProperty) {
        this.elevationProfileProperty = elevationProfileProperty;
        this.highlightedPositionProperty = highlightedPositionProperty;
        borderPane = new BorderPane();
        pane = new Pane();
        System.out.println(pane.widthProperty().get());
        borderPane.getStylesheets().add("elevation_profile.css");
        pane.getStylesheets().add("elevation_profile.css");
        borderPane.getChildren().add(pane);
        pane.widthProperty().addListener(l -> {
                    if (pane.getHeight() != 0) {
                        changeUnits();
                        changeConversion();
                        createPolygone();
                    }
                });
        pane.heightProperty().addListener(l -> {
                changeUnits();
                changeConversion();
                createPolygone();});
        worldToScreen = new SimpleObjectProperty<>();
        rectangleProfile = new SimpleObjectProperty<>();
        rectangleInsets = new Insets(10, 10, 20, 40);


        worldToScreen.addListener(l -> {
            try {
                screenToWorld.setValue(worldToScreen.get().createInverse());
            } catch (NonInvertibleTransformException exception) {
                throw new Error (exception);
            }
        });
        comprendreWhatTheHeckIsGoingOn();
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

    private void changeConversion() {
        Affine affine = new Affine();
        //affine.prependTranslation(rectangleInsets.getLeft(), rectangleInsets.getBottom());
        ElevationProfile elevationProfile = elevationProfileProperty.get();
//        affine.prependScale(elevationProfile.length() /
//                (pane.getWidth() - rectangleInsets.getLeft() - rectangleInsets.getRight()),
//                elevationProfile.maxElevation() /
//                        (pane.getHeight() -  rectangleInsets.getBottom() - rectangleInsets.getTop()));
        //affine.prependTranslation(0, elevationProfile.minElevation());
        affine.prependScale(pane.getWidth() / elevationProfile.length(),
                 pane.getHeight() / (elevationProfile.maxElevation()));
        //affine.prependTranslation(0, pane.getHeight());
        System.out.println("SX : " + pane.getWidth() / elevationProfile.length());
        System.out.println("SY : " + pane.getHeight() / (elevationProfile.maxElevation()));
        worldToScreen.setValue(affine);
    }

    private void createPolygone() {
        List<Double> listPoints = new ArrayList<>();
        double position = 0;
        listPoints.add(0.0);
        listPoints.add(0.0);
        while (position <= elevationProfileProperty.get().length()) {
            Point2D pointToTransform = new Point2D(position, elevationProfileProperty.get().elevationAt(position));
            Point2D pointToAdd = worldToScreen.get().transform(pointToTransform);
            listPoints. add(pointToAdd.getX());
            listPoints. add(pointToAdd.getY());
            position += xUnit;
        }
        listPoints.add((position - xUnit) / xUnit);
        listPoints.add(0.0);
        //System.out.println(listPoints);
        Polygon profile = new Polygon();
        profile.setId("profile");
        profile.getPoints().addAll(listPoints);
        pane.getChildren().clear();
        pane.getChildren().add(profile);
    }


    private void comprendreWhatTheHeckIsGoingOn() {
        Point2D test = new Point2D(100, 100);

        Affine affine1 = new Affine();
        affine1.prependScale(0.5, 2);
        System.out.println(affine1.transform(test));

        Affine affine2 = new Affine();
        affine2.prependTranslation(10,-10);
        System.out.println(affine2.transform(test));

        Point2D firstPoint = new Point2D(0, 663);
        System.out.println(firstPoint);

    }
}
