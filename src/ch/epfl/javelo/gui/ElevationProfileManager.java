package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import java.util.ArrayList;
import java.util.List;

public final class ElevationProfileManager {

    private ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty; //null to add
    private DoubleProperty mousePositionProperty; //Nan To add
    private ReadOnlyDoubleProperty highlightedPositionProperty;
    private BorderPane borderPane;
    private Pane pane;
    private VBox routeStatistics;
    private ObjectProperty<Rectangle2D> rectangleProperty;
    private Path grid;
    private Line line;
    //private ObjectProperty<Text> statsProperty;
    private Text stats;
    private Polygon profile;
    private Group texts;
    private Insets insets;
    private ObjectProperty<Transform> worldToScreen;
    private ObjectProperty<Transform> screenToWorld;
    long t0;

    private final static int[] POS_STEPS =
            { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
    private final static int[] ELE_STEPS =
            { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ReadOnlyDoubleProperty highlightedPositionProperty) {
        this.elevationProfileProperty = elevationProfileProperty;
        this.highlightedPositionProperty = highlightedPositionProperty;
        mousePositionProperty = new SimpleDoubleProperty();
        insets = new Insets(10, 10, 20, 40);
        rectangleProperty = new SimpleObjectProperty<>(new Rectangle2D(0,0,0,0)); //ADD CTS
        profile = new Polygon();
        profile.setId("profile");
        grid = new Path();
        grid.setId("grid");
        texts = new Group();
        line = new Line();
        //statsProperty = new SimpleObjectProperty<>(new Text());
        stats = new Text();
        //routeStatistics = new VBox(statsProperty.get());
        routeStatistics = new VBox(stats);
        routeStatistics.setId("profile_data");
        screenToWorld = new SimpleObjectProperty<>();
        worldToScreen = new SimpleObjectProperty<>();
        Affine first = new Affine();
        first.prependTranslation(10,10);
        screenToWorld.setValue(first);
        try {
            worldToScreen.setValue(screenToWorld.get().createInverse());
        } catch (NonInvertibleTransformException exception){
            throw new Error(exception);
        }
        pane = new Pane(profile, line, grid, texts);
        borderPane = new BorderPane(pane, null, null, routeStatistics, null);
        borderPane.getStylesheets().add("elevation_profile.css");


        System.out.printf("Début avant createStats\n",
                (System.nanoTime() - t0) / 1_000_000);


        //statsBinding();
        rectangleBinding();
        elevationProfileProperty.addListener(l -> {
            if (elevationProfileProperty.get() != null) {
                transformations();
                createStatistics();
            }});
        rectangleProperty.addListener(l -> {
            //FIND UNE AUTRE SOLUTION
            if(elevationProfileProperty.get() != null) {
                 transformations();
            }});
        worldToScreen.addListener(l -> {
            if (rectangleProperty.get().getHeight() != 0) {
                createPolygon();
                createGrid();
                createStatistics();
            }
            });
        screenToWorld.addListener(l -> {
            if (rectangleProperty.get().getHeight() != 0) {
                createPolygon();
                createGrid();
                createStatistics();
            }
        });
        lineBindings();
        events();
    }

    public Pane pane() {
        return borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePositionProperty;
    }

    private void lineBindings() {
        line.visibleProperty().bind(highlightedPositionProperty.greaterThanOrEqualTo(0));
        line.layoutXProperty().bind(Bindings.createDoubleBinding( () ->
                        worldToScreen.get().transform(new Point2D(highlightedPositionProperty.get(), 0)).getX(),
                worldToScreen, highlightedPositionProperty));
        line.startYProperty().bind(Bindings.select(rectangleProperty, "minY"));
        line.endYProperty().bind(Bindings.select(rectangleProperty, "maxY"));
    }

    private void events() {
        pane.setOnMouseMoved(e -> {
            if (rectangleProperty.get().contains(new Point2D(e.getX(), e.getY()))) {
                mousePositionProperty.set(screenToWorld.get().transform(new Point2D(e.getX(), 0)).getX());
            } else {
                mousePositionProperty.set(Double.NaN);
            }
                });
        pane.setOnMouseExited(e ->
            mousePositionProperty.set(Double.NaN));
    }

    private void createGrid() {
        texts.getChildren().clear();
        grid.getElements().clear();
        int posStep = 0;
        int eleStep = 0;
        double posSpacing = 0;
        double eleSpacing = 0;
        if(worldToScreen.get() != null) {
            for (int i = 0; i < POS_STEPS.length; ++i) {
                posSpacing = worldToScreen.get().deltaTransform(POS_STEPS[i], 0).getX();
                posStep = POS_STEPS[i];
                if (posSpacing >= 50) {
                    break;
                }
            }
            for (int i = 0; i < ELE_STEPS.length; ++i) {
                eleSpacing = worldToScreen.get().deltaTransform(0, -ELE_STEPS[i]).getY();
                eleStep = ELE_STEPS[i];
                if (eleSpacing >= 25) {
                    break;
                }
            }
            double xPosition = insets.getLeft();
            int positionInText = 0;
            if (posStep != 0) {
                for (int i = 0; i < Math.ceil(elevationProfileProperty.get().length() / posStep); ++i) {
                    grid.getElements().add(new MoveTo(xPosition, insets.getTop()));
                    grid.getElements().add(new LineTo(xPosition, insets.getTop() + rectangleProperty.get().getHeight()));
                    Text posText = new Text();
                    posText.getStyleClass().add("grid_label");
                    posText.getStyleClass().add("horizontal");
                    posText.textOriginProperty().setValue(VPos.TOP);
                    posText.setText(String.valueOf(positionInText));
                    posText.setFont(Font.font("Avenir", 10));
                    posText.setLayoutX(xPosition - posText.prefWidth(0) / 2);
                    posText.setLayoutY(insets.getTop() + rectangleProperty.get().getHeight());
                    texts.getChildren().add(posText);
                    positionInText += posStep / 1000;
                    xPosition += posSpacing;
                }
            }
            double gapM = elevationProfileProperty.get().minElevation() % eleStep;
            double gapP = worldToScreen.get().deltaTransform(0, gapM).getY();
            double yPosition = insets.getTop() + rectangleProperty.get().getHeight() + gapP;
            int elevationInText = (int) Math.ceil(elevationProfileProperty.get().minElevation() / eleStep)
                    * eleStep;
            if (eleStep != 0) {
                for (int i = 0; i < Math.ceil(elevationProfileProperty.get().maxElevation() - elevationProfileProperty.get().minElevation()
                        / eleStep); ++i) {
                    grid.getElements().add(new MoveTo(insets.getLeft(), yPosition));
                    grid.getElements().add(new LineTo(insets.getLeft() + rectangleProperty.get().getWidth(), yPosition));
                    Text eleText = new Text();
                    eleText.getStyleClass().add("grid_label");
                    eleText.getStyleClass().add("verticale");
                    eleText.textOriginProperty().setValue(VPos.CENTER);
                    eleText.setText(String.valueOf(elevationInText));
                    eleText.setFont(Font.font("Avenir", 10));
                    eleText.setLayoutX(insets.getLeft() - eleText.prefWidth(0) - 2);
                    eleText.setLayoutY(yPosition);
                    texts.getChildren().add(eleText);
                    elevationInText += eleStep;
                    yPosition -= eleSpacing;
                }
            }
        }
    }

    private void rectangleBinding() {
        rectangleProperty.bind(Bindings.createObjectBinding((() -> {
                return new Rectangle2D(
                        insets.getLeft(),
                        insets.getTop(),
                        Math.max(pane.getWidth() - insets.getRight() - insets.getLeft(),0),
                        Math.max(pane.getHeight() - insets.getTop() - insets.getBottom(),0));
        }), pane.widthProperty(), pane.heightProperty()));
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

    private void createPolygon() {
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
        profile.getPoints().setAll(listPoints);
    }

    private void createStatistics() {
        ElevationProfile elevationProfile = elevationProfileProperty.get();
        if (elevationProfile != null) {
            stats.setText(String.format("Longueur : %.1f km" +
                            "     Montée : %.0f m" +
                            "     Descente : %.0f m" +
                            "     Altitude : de %.0f m à %.0f m",
                    elevationProfile.length() / 1000,
                    elevationProfile.totalAscent(),
                    elevationProfile.totalDescent(),
                    elevationProfile.minElevation(),
                    elevationProfile.maxElevation()));
        }
    }
}
