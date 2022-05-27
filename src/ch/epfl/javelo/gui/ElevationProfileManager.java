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

    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private final ReadOnlyDoubleProperty highlightedPositionProperty;
    private DoubleProperty mousePositionProperty;
    private ObjectProperty<Rectangle2D> rectangleProperty;
    private ObjectProperty<Transform> worldToScreen;
    private ObjectProperty<Transform> screenToWorld;

    private BorderPane borderPane;
    private Pane pane;
    private Path grid;
    private Line line;
    private Text stats;
    private Polygon profile;
    private Group texts;

    /**
     * Value of text's size
     */
    private final static int TEXT_SIZE = 10;
    /**
     * Minimum value of spacing between positions
     */
    private final static int MIN_POS_SPACING = 50;
    /**
     * Minimum value of spacing between elevations
     */
    private final static int MIN_ELE_SPACING = 25;
    /**
     * Dimensionless factor used to convert values in meters to kilometers
     */
    private final static double METER_KILOMETER_RATIO = 1d/1000d;
    /**
     * Distance in pixel subtracted to the position of the elevation
     */
    private final static int ELEVATION_LEFT_SHIFT = -2;
    /**
     * Dimensionless factor used to obtain the half of an existing value
     */
    private final static double HALF_RATIO = 1d/2d;

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
     * Style class for elevations
     */
    private final static String VERTICAL = "vertical";
    /**
     * Style class for positions
     */
    private final static String HORIZONTAL = "horizontal";
    /**
     * Style class for labels
     */
    private final static String GRID_LABEL = "grid_label";
    /**
     * Label's font
     */
    private final static String FONT_AVENIR = "Avenir";

    /**
     * Contains the distance between the top edge of the blue rectangle and the top edge
     * of the panel, as well as the distance between their two right edges, their left edges and
     * their bottom edges
     */
    private final static Insets FRAME_AND_PANE_GAP = new Insets(10, 10, 20, 40);

    /**
     * Constructs an elevation profile manager
     * @param elevationProfileProperty a read-only property, containing the profile to
     * be displayed or null in case no profile is to be displayed
     * @param highlightedPositionProperty a read-only property containing the position along
     * the profile to be highlighted or NaN in case no position is to be highlighted
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile>
                                           elevationProfileProperty,
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
        if(elevationProfile() != null) {
            affine.prependTranslation(-FRAME_AND_PANE_GAP.getLeft(), -FRAME_AND_PANE_GAP.getTop());
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
        double positionP = FRAME_AND_PANE_GAP.getLeft();

            listPoints.add(FRAME_AND_PANE_GAP.getLeft());
            listPoints.add(FRAME_AND_PANE_GAP.getTop() + rectangle().getHeight());
            while (positionP <= rectangle().getWidth() + FRAME_AND_PANE_GAP.getLeft()) {
                Point2D pointP = new Point2D(positionP, 0);
                Point2D pointM = screenToWorld().transform(pointP);
                Point2D pointToTransform = new Point2D(pointM.getX(),
                        elevationProfile().elevationAt(pointM.getX()));
                Point2D pointToAdd = worldToScreen().transform(pointToTransform);
                listPoints.add(pointToAdd.getX());
                listPoints.add(pointToAdd.getY());
                positionP += 1;
            }
            listPoints.add(FRAME_AND_PANE_GAP.getLeft() + rectangle().getWidth());
            listPoints.add(FRAME_AND_PANE_GAP.getTop() + rectangle().getHeight());

        profile.getPoints().setAll(listPoints);
    }

    /**
     * Determines the values to use to separate vertical and horizontal lines of position
     *  and altitude respectively, creates a grid accordingly and places position labels
     */
    private void createGrid() {
        texts.getChildren().clear();
        grid.getElements().clear();

        int posStep = 0;
        int eleStep = 0;
        double posSpacing = 0;
        double eleSpacing = 0;

        if(worldToScreen() != null) {
            for (int step : POS_STEPS) {
                posSpacing = worldToScreen().deltaTransform(step, 0).getX();
                posStep = step;
                if (posSpacing >= MIN_POS_SPACING) {
                    break;
                }
            }
            for (int step : ELE_STEPS) {
                eleSpacing = worldToScreen().deltaTransform(0, -step).getY();
                eleStep = step;
                if (eleSpacing >= MIN_ELE_SPACING) {
                    break;
                }
            }

            double xPosition = FRAME_AND_PANE_GAP.getLeft();
            int positionInText = 0;
            double posCondition = Math.ceil(elevationProfile().length() / posStep);
            for (int i = 0; i < posCondition; ++i) {
                grid.getElements().add(new MoveTo(xPosition, FRAME_AND_PANE_GAP.getTop()));
                grid.getElements().add(new LineTo(xPosition, FRAME_AND_PANE_GAP.getTop() +
                        rectangle().getHeight()));

                setPosText(positionInText, xPosition);

                positionInText += posStep * METER_KILOMETER_RATIO;
                xPosition += posSpacing;
            }

            int elevationInText = (int) Math.ceil(elevationProfile().minElevation() / eleStep)
                    * eleStep;
            double gapM = elevationInText - elevationProfile().minElevation();
            double gapP = worldToScreen().deltaTransform(0, gapM).getY();
            double yPosition = FRAME_AND_PANE_GAP.getTop() + rectangle().getHeight() + gapP;
            double eleCondition = Math.ceil(elevationProfile().maxElevation()
                    - elevationProfile().minElevation() / eleStep) - 1;
            for (int i = 0; i < eleCondition; ++i) {
                grid.getElements().add(new MoveTo(FRAME_AND_PANE_GAP.getLeft(), yPosition));
                grid.getElements().add(new LineTo(
                        FRAME_AND_PANE_GAP.getLeft() + rectangle().getWidth(), yPosition));
                setEleText(elevationInText, yPosition);
                elevationInText += eleStep;
                yPosition -= eleSpacing;
            }
        }
    }

    /**
     * Sets the position's text
     * @param positionInText the value of the position
     * @param xPosition the position of the text
     */
    public void setPosText(int positionInText, double xPosition) {
        Text posText = new Text();
        posText.getStyleClass().add(GRID_LABEL);
        posText.getStyleClass().add(HORIZONTAL);
        posText.textOriginProperty().setValue(VPos.TOP);
        posText.setText(String.valueOf(positionInText));
        posText.setFont(Font.font(FONT_AVENIR, TEXT_SIZE));
        posText.setLayoutX(xPosition - posText.prefWidth(0) * HALF_RATIO);
        posText.setLayoutY(FRAME_AND_PANE_GAP.getTop() + rectangle().getHeight());
        texts.getChildren().add(posText);
    }

    /**
     * Sets the elevation's text
     * @param elevationInText the value of the elevation
     * @param yPosition the position of the text
     */
    public void setEleText(int elevationInText, double yPosition) {
        Text eleText = new Text();
        eleText.getStyleClass().add(GRID_LABEL);
        eleText.getStyleClass().add(VERTICAL);
        eleText.textOriginProperty().setValue(VPos.CENTER);
        eleText.setText(String.valueOf(elevationInText));
        eleText.setFont(Font.font(FONT_AVENIR, TEXT_SIZE));
        eleText.setLayoutX(FRAME_AND_PANE_GAP.getLeft() - eleText.prefWidth(0) + ELEVATION_LEFT_SHIFT);
        eleText.setLayoutY(yPosition);
        texts.getChildren().add(eleText);
    }
    /**
     * Adds the route statistics to the bottom of the panel
     */
    private void createStatistics() {
        if (elevationProfile() != null) {
            stats.setText(String.format("Longueur : %.1f km"
                            + "     Montée : %.0f m"
                            + "     Descente : %.0f m"
                            + "     Altitude : de %.0f m à %.0f m",
                    elevationProfile().length() * METER_KILOMETER_RATIO,
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
        mousePositionProperty = new SimpleDoubleProperty(Double.NaN);
        rectangleProperty = new SimpleObjectProperty<>(Rectangle2D.EMPTY);

        profile = new Polygon();
        profile.setId("profile");

        grid = new Path();
        grid.setId("grid");

        texts = new Group();
        line = new Line();

        stats = new Text();
        VBox routeStatistics = new VBox(stats);
        routeStatistics.setId("profile_data");

        screenToWorld = new SimpleObjectProperty<>(new Affine());
        worldToScreen = new SimpleObjectProperty<>(new Affine());

        pane = new Pane(profile, line, grid,texts);
        borderPane = new BorderPane(pane, null, null, routeStatistics, null);
        borderPane.getStylesheets().add("elevation_profile.css");
    }

    /**
     * Adds listeners to the properties containing : the transformation worldToScreen,
     * the elevation profile and the rectangle that contains it
     */
    private void listeners() {
        elevationProfileProperty.addListener(l -> {
            if (elevationProfile() != null) {
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
        rectangleProperty.bind(Bindings.createObjectBinding((() ->new Rectangle2D(
                    FRAME_AND_PANE_GAP.getLeft(),
                    FRAME_AND_PANE_GAP.getTop(),
                    Math.max(pane.getWidth() - FRAME_AND_PANE_GAP.getRight() - FRAME_AND_PANE_GAP.getLeft(),0),
                    Math.max(pane.getHeight() - FRAME_AND_PANE_GAP.getTop() - FRAME_AND_PANE_GAP.getBottom(),0))),
                pane.widthProperty(), pane.heightProperty()));
    }

    /**
     * Binds the properties of the line with the properties containing : the highlighted position,
     * the transformation worldToScreen and the rectangle containing the elevation profile
     */
    private void lineBindings() {
        line.visibleProperty().bind(highlightedPositionProperty.greaterThanOrEqualTo(0));
        line.layoutXProperty().bind(Bindings.createDoubleBinding( () ->
                        worldToScreen().transform(new Point2D(
                                highlightedPositionProperty.get(), 0)).getX(),
                worldToScreen, highlightedPositionProperty));
        line.startYProperty().bind(Bindings.select(rectangleProperty, "minY"));
        line.endYProperty().bind(Bindings.select(rectangleProperty, "maxY"));
    }

    /**
     * Manages the events related to the mouse
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

    /**
     * Returns the rectangle contained in the property
     * @return a rectangle
     */
    private Rectangle2D rectangle() {
        return rectangleProperty.get();
    }

    /**
     * Returns the elevation profile contained in the property
     * @return an elevation profile
     */
    private ElevationProfile elevationProfile() {
        return elevationProfileProperty.get();
    }

    /**
     * Returns the transform contained in the property worldToScreen
     * @return a transform
     */
    private Transform worldToScreen() {
        return worldToScreen.get();
    }

    /**
     * Returns the transform contained in the property screenToWorld
     * @return a transform
     */
    private Transform screenToWorld() {
        return screenToWorld.get();
    }
}

