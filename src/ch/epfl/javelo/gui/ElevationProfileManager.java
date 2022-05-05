package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import java.util.ArrayList;
import java.util.List;

public final class ElevationProfileManager {

    private ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty; //null to add
    private ReadOnlyDoubleProperty highlightedPositionProperty; //nan to add
    private BorderPane borderPane;
    private Pane pane;
    private VBox routeProperties;
    private ObjectProperty<Rectangle2D> rectangleProperty;
    private Insets insets;
    private ObjectProperty<Transform> worldToScreen;
    private ObjectProperty<Transform> screenToWorld;

    //Question : rectangle pas bien dimensionné au départ. Chelou.
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ReadOnlyDoubleProperty highlightedPositionProperty) {
        this.elevationProfileProperty = elevationProfileProperty;
        this.highlightedPositionProperty = highlightedPositionProperty;
        insets = new Insets(10, 10, 20, 40);
        rectangleProperty = new SimpleObjectProperty<>();
        screenToWorld = new SimpleObjectProperty<>();
        worldToScreen = new SimpleObjectProperty<>();
        pane = new Pane();
        routeProperties = new VBox();
        borderPane = new BorderPane();
        routeProperties.setId("profile_data");
        borderPane.getStylesheets().add("elevation_profile.css");
        borderPane.setCenter(pane);
        borderPane.setBottom(routeProperties);

        //IMPORTANT: ADD LA TECHNIQUE STARTY AND ENDY ASSISTANT
        pane.widthProperty().addListener(l -> bindRectangleProperty());
        pane.heightProperty().addListener(l -> bindRectangleProperty());
        rectangleProperty.addListener(l -> {
            transformations();});
        worldToScreen.addListener(l -> createPolygone());
        screenToWorld.addListener(l -> createPolygone());
    }

    public Pane pane() {
        return borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty(){
        //ToDo
        return null;
    }

    private void bindRectangleProperty() {
        rectangleProperty.bind(Bindings.createObjectBinding((() -> {
            if(pane.getWidth() > 0 && pane.getHeight() > 0) {
                return new Rectangle2D(
                        insets.getLeft(),
                        insets.getTop(),
                        pane.getWidth() - insets.getRight() - insets.getLeft(),
                        pane.getHeight() - insets.getTop() - insets.getBottom());
            } else {
                return new Rectangle2D(insets.getLeft(), insets.getTop(),
                        0,0);
            }
        })));
    }

    private void transformations() {
        Affine affine = new Affine();
        ElevationProfile elevationProfile = elevationProfileProperty.get();
        affine.prependTranslation(-insets.getLeft(), -insets.getTop());
        affine.prependScale(
                elevationProfile.length() / rectangleProperty.get().getWidth(),
                (elevationProfile.minElevation() - elevationProfile.maxElevation()) / rectangleProperty.get().getHeight());
        affine.prependTranslation(0, elevationProfile.maxElevation());
        screenToWorld.setValue(affine);
        try {
            worldToScreen.setValue(screenToWorld.get().createInverse());
        } catch (NonInvertibleTransformException exception) {
            throw new Error (exception);
        }
    }

    private void createPolygone() {
        List<Double> listPoints = new ArrayList<>();
        Rectangle2D rectangle = rectangleProperty.get();
        ElevationProfile elevationProfile = elevationProfileProperty.get();
        double positionP = insets.getLeft();
        if(worldToScreen.get() != null) {
            listPoints.add(insets.getLeft());
            listPoints.add(insets.getTop() + rectangle.getHeight());
            while (positionP <= rectangleProperty.get().getWidth() + insets.getLeft()) {
                Point2D pointP = new Point2D(positionP, 0);
                Point2D pointM = screenToWorld.get().transform(pointP);
                Point2D pointToTransform = new Point2D(pointM.getX(), elevationProfile.elevationAt(pointM.getX()));
                Point2D pointToAdd = worldToScreen.get().transform(pointToTransform);

                listPoints.add(pointToAdd.getX());
                listPoints.add(pointToAdd.getY());
                positionP += 1;
            }
            listPoints.add(insets.getLeft() + rectangle.getWidth());
            listPoints.add(insets.getTop() + rectangle.getHeight());
        }
        Polygon profile = new Polygon();
        profile.setId("profile");
        profile.getPoints().addAll(listPoints);
        pane.getChildren().clear();
        pane.getChildren().add(profile);
    }
}
