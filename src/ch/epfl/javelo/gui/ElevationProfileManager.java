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
import javafx.scene.Node;
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
    private ObjectProperty<Rectangle2D> rectangleProperty;
    private Insets insets;
    private ObjectProperty<Transform> worldToScreen;
    private ObjectProperty<Transform> screenToWorld;
    private double xUnit;
    //private double yUnit;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ReadOnlyDoubleProperty highlightedPositionProperty) {
        this.elevationProfileProperty = elevationProfileProperty;
        this.highlightedPositionProperty = highlightedPositionProperty;
        insets = new Insets(10, 10, 20, 40);
        rectangleProperty = new SimpleObjectProperty<>();
        screenToWorld = new SimpleObjectProperty<>();
        worldToScreen = new SimpleObjectProperty<>();
        pane = new Pane();
        borderPane = new BorderPane();

        borderPane.getStylesheets().add("elevation_profile.css");
        borderPane.getChildren().add(pane);

        //Il faut utiliser des bindings ici et non des listener pour que le rectangle bleu soit toujours
        // redimensionné en fonction du borderPane
        rectangleProperty.bind(Bindings.createObjectBinding((() -> {
            System.out.println("dans bind");
            if(borderPane.getWidth() > 0 && borderPane.getHeight() > 0) {
                return new Rectangle2D(insets.getLeft(), insets.getTop(),
                        borderPane.getWidth() - insets.getRight() - insets.getLeft(),
                        borderPane.getHeight() - insets.getTop() - insets.getBottom());
            } else {
                return new Rectangle2D(insets.getLeft(), insets.getTop(),
                        0,0);
            }
        })));

        //Succession d'appels comme expliqué dans le post piazza que j'ai send sur le groupe
        rectangleProperty.addListener(l -> {
            System.out.println("listener de rectangle property");
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

    private void transformations() {
        Affine affine = new Affine();
        ElevationProfile elevationProfile = elevationProfileProperty.get();
        //Première translation qui permet de passer du pane à l'intérieur du rectangle bleu
        affine.prependTranslation(-insets.getLeft(), -insets.getTop());
        //Scaling par rapport à la taille du rectangle bleu (le "-" avant celui du y permet d'appliquer directly
        //la deuxième translation sur la valeur donnée par le scaling)
        affine.prependScale(
                elevationProfile.length() / rectangleProperty.get().getWidth(),
                - elevationProfile.maxElevation() / rectangleProperty.get().getHeight());
        //Deuxième translation permettant d'inverser le y
        affine.prependTranslation(0, rectangleProperty.get().getHeight());
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
        double height = rectangle.getHeight();
        double width = rectangle.getWidth();
        double minX = rectangle.getMinX();
        double minY = rectangle.getMinY();
        double maxX = rectangle.getMaxX();
        double maxY = rectangle.getMaxY();
        //4 points du rectangle pour voir déjà/debugger si son affichage se fait bien comme il faut
        /*listPoints.add(minX);
        listPoints.add(minY + height);
        listPoints.add(minX);
        listPoints.add(minY);
        listPoints.add(minX + width);
        listPoints.add(minY);
        listPoints.add(maxX);
        listPoints.add(maxY);*/
        ElevationProfile elevationProfile = elevationProfileProperty.get();
        double positionP = 0;
        //Valeurs récupérées directement à partir du rectangle
        listPoints.add(minX);
        listPoints.add(minY + height);
        if(worldToScreen.get() != null) {
            while (positionP <= rectangleProperty.get().getWidth()) {
                //Ces deux variables permettent de convertir la position des pxels aux mètres pour use cette value
                //dans le pointToTransform
                //Le 0 est une valeur par défaut mais inévitable pour le passage d'un système de coordo à un autre
                Point2D pointP = new Point2D(positionP, 0);
                Point2D pointM = screenToWorld.get().transform(pointP);
                Point2D pointToTransform = new Point2D(pointM.getX(), elevationProfile.elevationAt(pointM.getX()));
                Point2D pointToAdd = worldToScreen.get().transform(pointToTransform);
                listPoints.add(pointToAdd.getX());
                listPoints.add(pointToAdd.getY());
                //Il ne faut incrémenter que d'une seule unité (un pixel)
                positionP += 1;
            }
        }
        //Valeurs récupérées directement à partir du rectangle
        listPoints.add(maxX);
        listPoints.add(maxY);
        Polygon profile = new Polygon();
        profile.setId("profile");
        profile.getPoints().addAll(listPoints);
        pane.getChildren().clear();
        pane.getChildren().add(profile);
    }
}
