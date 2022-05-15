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

/**
 * Manages the display of the longitudinal profile of the route and the interaction with it
 * @author Hamane Aya (345565)
 * @author Sadgal Lina (342075)
 */
public final class ElevationProfileManager {

    private ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private ReadOnlyDoubleProperty highlightedPositionProperty;
    private DoubleProperty mousePositionProperty;
    private ObjectProperty<Rectangle2D> rectangleProperty;
    private ObjectProperty<Transform> worldToScreen;
    private ObjectProperty<Transform> screenToWorld;

    private BorderPane borderPane;
    private Pane pane;
    private VBox routeStatistics;
    private Path grid;
    private Line line;
    private Text stats;
    private Polygon profile;
    private Group texts;
    private Insets insets;

    /**
     * Table containing the different values that can be used to separate the vertical lines of position
     */
    private final static int[] POS_STEPS =
            { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
    /**
     * Table containing the different values that can be used to separate the horizontal lines of altitude
     */
    private final static int[] ELE_STEPS =
            { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };

    /**
     * Constructs an elevation profile manager
     * @param elevationProfileProperty a read-only property, containing the profile to
     * be displayed or null in case no profile is to be displayed
     * @param highlightedPositionProperty a read-only property containing the position along
     * the profile to be highlighted or NaN in case no position is to be highlighted
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ReadOnlyDoubleProperty highlightedPositionProperty) {
        this.elevationProfileProperty = elevationProfileProperty;
        this.highlightedPositionProperty = highlightedPositionProperty;
        initialize();
        listeners();
        rectangleBinding();
        lineBindings();
        events();
    }

    /**
     * Returns the panel containing the profile design
     * @return the border pane
     */
    public Pane pane() {
        return borderPane;
    }
    
    private Rectangle2D rectangle() {
        return rectangleProperty.get();
    }

    private ElevationProfile elevationProfile() {
        return elevationProfileProperty.get();
    }

    private Transform worldToScreen() {
        return worldToScreen.get();
    }

    private Transform screenToWorld() {
        return screenToWorld.get();
    }
    /**
     * Returns a read-only property containing the position of the mouse pointer along the profile
     * (in meters, rounded to the nearest integer) or NaN if the mouse pointer is not above the
     * profile
     * @return the property of the mouse position on the profile
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePositionProperty;
    }

    /**
     * Computes the value of the transformation screenToWorld and its inverse worldToScreen,
     * each of which consists of a succession of translations and a scaling
     */
    private void transformations() {
        Affine affine = new Affine();
        Rectangle2D rectangle = rectangleProperty.get();
        ElevationProfile elevationProfile = elevationProfileProperty.get();

        if(elevationProfile() != null) {
            affine.prependTranslation(-insets.getLeft(), -insets.getTop());
            affine.prependScale(
                    elevationProfile().length() / rectangle().getWidth(),
                    (elevationProfile().minElevation()
                            - elevationProfile().maxElevation()) / rectangle().getHeight());
            affine.prependTranslation(0, elevationProfile().maxElevation());
            screenToWorld.setValue(affine);
            try {
                worldToScreen.setValue(screenToWorld().createInverse());
            } catch (NonInvertibleTransformException exception) {
                throw new Error (exception);
            }
        }
    }

    /**
     * Creates a polygon representing the profile by constructing its list of points
     */
    private void createPolygon() {
        List<Double> listPoints = new ArrayList<>();
        Rectangle2D rectangle = rectangleProperty.get();
        ElevationProfile elevationProfile = elevationProfileProperty.get();
        double positionP = insets.getLeft();

        if(worldToScreen.get() != null) {
            listPoints.add(insets.getLeft());
            listPoints.add(insets.getTop() + rectangle().getHeight());
            while (positionP <= rectangle().getWidth() + insets.getLeft()) {
                Point2D pointP = new Point2D(positionP, 0);
                Point2D pointM = screenToWorld().transform(pointP);
                Point2D pointToTransform = new Point2D(pointM.getX(), elevationProfile().elevationAt(pointM.getX()));
                Point2D pointToAdd = worldToScreen().transform(pointToTransform);
                listPoints.add(pointToAdd.getX());
                listPoints.add(pointToAdd.getY());
                positionP += 1;
            }
            listPoints.add(insets.getLeft() + rectangle().getWidth());
            listPoints.add(insets.getTop() + rectangle().getHeight());
        }

        profile.getPoints().setAll(listPoints);
    }

    /**
     * Determines the values to use to separate vertical and horizontal lines of position and altitude
     * respectively, creates a grid accordingly and places position labels
     */
    private void createGrid() {
        texts.getChildren().clear();
        grid.getElements().clear();

        int posStep = 0;
        int eleStep = 0;
        double posSpacing = 0;
        double eleSpacing = 0;

        if(worldToScreen() != null) {
            for (int i = 0; i < POS_STEPS.length; ++i) {
                posSpacing = worldToScreen().deltaTransform(POS_STEPS[i], 0).getX();
                posStep = POS_STEPS[i];
                if (posSpacing >= 50) {
                    break;
                }
            }
            for (int i = 0; i < ELE_STEPS.length; ++i) {
                eleSpacing = worldToScreen().deltaTransform(0, -ELE_STEPS[i]).getY();
                eleStep = ELE_STEPS[i];
                if (eleSpacing >= 25) {
                    break;
                }
            }

            double xPosition = insets.getLeft();
            int positionInText = 0;

            for (int i = 0; i < Math.ceil(elevationProfile().length() / posStep); ++i) {

                grid.getElements().add(new MoveTo(xPosition, insets.getTop()));
                grid.getElements().add(new LineTo(xPosition, insets.getTop() + rectangle().getHeight()));

                Text posText = new Text();
                posText.getStyleClass().add("grid_label");
                posText.getStyleClass().add("horizontal");
                posText.textOriginProperty().setValue(VPos.TOP);
                posText.setText(String.valueOf(positionInText));
                posText.setFont(Font.font("Avenir", 10));
                posText.setLayoutX(xPosition - posText.prefWidth(0) / 2);
                posText.setLayoutY(insets.getTop() + rectangle().getHeight());
                texts.getChildren().add(posText);

                positionInText += posStep / 1000;
                xPosition += posSpacing;
            }

            double gapM = elevationProfile().minElevation() % eleStep;
            double gapP = worldToScreen().deltaTransform(0, gapM).getY();

            double yPosition = insets.getTop() + rectangle().getHeight() + gapP;
            int elevationInText = (int) Math.ceil(elevationProfile().minElevation() / eleStep) * eleStep;

            for (int i = 0; i < Math.ceil(elevationProfile().maxElevation() - elevationProfile().minElevation()
                    / eleStep); ++i) {

                grid.getElements().add(new MoveTo(insets.getLeft(), yPosition));
                grid.getElements().add(new LineTo(insets.getLeft() + rectangle().getWidth(), yPosition));

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

    /**
     * Adds the route statistics to the bottom of the panel
     */
    private void createStatistics() {
        ElevationProfile elevationProfile = elevationProfileProperty.get();
        if (elevationProfile() != null) {
            stats.setText(String.format("Longueur : %.1f km" +
                            "     Montée : %.0f m" +
                            "     Descente : %.0f m" +
                            "     Altitude : de %.0f m à %.0f m",
                    elevationProfile().length() / 1000,
                    elevationProfile().totalAscent(),
                    elevationProfile().totalDescent(),
                    elevationProfile().minElevation(),
                    elevationProfile().maxElevation()));
        }
    }

    /**
     * Initializes all the attributes of the class ElevationProfileManager
     */
    private void initialize() {
        mousePositionProperty = new SimpleDoubleProperty();

        insets = new Insets(10, 10, 20, 40);
        rectangleProperty = new SimpleObjectProperty<>(Rectangle2D.EMPTY);

        profile = new Polygon();
        profile.setId("profile");

        grid = new Path();
        grid.setId("grid");

        texts = new Group();
        line = new Line();

        stats = new Text();
        routeStatistics = new VBox(stats);
        routeStatistics.setId("profile_data");

        screenToWorld = new SimpleObjectProperty<>(new Affine());
        worldToScreen = new SimpleObjectProperty<>(new Affine());

        pane = new Pane(profile, line, grid, texts);
        borderPane = new BorderPane(pane, null, null, routeStatistics, null);
        borderPane.getStylesheets().add("elevation_profile.css");
    }

    /**
     * Adds listeners to the properties containing : the transformation worldToScreen, the elevation profile
     * and the rectangle that contains it
     */
    private void listeners() {
        elevationProfileProperty.addListener(l -> {
            if (elevationProfileProperty.get() != null) {
                transformations();
                createStatistics();
            }
        });

        rectangleProperty.addListener(l ->
            transformations());

        worldToScreen.addListener(l -> {
            createPolygon();
            createGrid();
            createStatistics();
        });
    }

    /**
     * Binds the rectangle containing the elevation profile and the pane
     */
    private void rectangleBinding() {
        rectangleProperty.bind(Bindings.createObjectBinding((() -> {
            return new Rectangle2D(
                    insets.getLeft(),
                    insets.getTop(),
                    Math.max(pane.getWidth() - insets.getRight() - insets.getLeft(),0),
                    Math.max(pane.getHeight() - insets.getTop() - insets.getBottom(),0));
        }), pane.widthProperty(), pane.heightProperty()));
    }

    /**
     * Binds the properties of the line with the properties containing : the highlighted position,
     * the transformation worldToScreen and the rectangle containing the elevation profile
     */
    private void lineBindings() {
        line.visibleProperty().bind(highlightedPositionProperty.greaterThanOrEqualTo(0));
        line.layoutXProperty().bind(Bindings.createDoubleBinding( () ->
                        worldToScreen().transform(new Point2D(highlightedPositionProperty.get(), 0))
                                       .getX(),
                worldToScreen, highlightedPositionProperty));
        line.startYProperty().bind(Bindings.select(rectangleProperty, "minY"));
        line.endYProperty().bind(Bindings.select(rectangleProperty, "maxY"));
    }

    /**
     * Manage the events related to the mouse
     */
    private void events() {
        pane.setOnMouseMoved(e -> {
            if (rectangle().contains(new Point2D(e.getX(), e.getY()))) {
                mousePositionProperty.set(
                                       screenToWorld().transform(new Point2D(e.getX(), 0))
                                                      .getX());
            } else {
                mousePositionProperty.set(Double.NaN);
            }
        });
        pane.setOnMouseExited(e ->
                mousePositionProperty.set(Double.NaN));
    }
}

