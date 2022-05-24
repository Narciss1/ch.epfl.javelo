package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public final class ErrorManager {

    private final VBox errorPane;
    private final Text errorText;

    ///!\Pour moi le code est bcp plus clair comme ça (ça éviterait notamment de surcharger la méthode
    //display). Qu'en pense le prof ?
    private final FadeTransition firstFdeTransition;
    private final PauseTransition pauseTransition;
    private final FadeTransition lastFadeTransition;
    private final SequentialTransition sequentialTransition;

    /**
     * Constructor
     */
    public ErrorManager() {
        errorPane = new VBox();
        errorPane.getStylesheets().add("error.css");
        errorPane.setMouseTransparent(true);
        errorText = new Text();
        errorPane.getChildren().add(errorText);
        firstFdeTransition = new FadeTransition(new Duration(200), errorPane);
        firstFdeTransition.setFromValue(0);
        firstFdeTransition.setToValue(0.8);
        pauseTransition = new PauseTransition(new Duration(2000));
        lastFadeTransition = new FadeTransition(new Duration(500), errorPane);
        lastFadeTransition.setFromValue(0.8);
        lastFadeTransition.setToValue(0);
        sequentialTransition = new SequentialTransition(firstFdeTransition,
                pauseTransition, lastFadeTransition);
    }

    /**
     *
     * @return the plans on which the errors' message appears.
     */
    public Pane pane() {
        return errorPane;
    }

    /**
     * makes the error's message briefly appears on the pane with an error sound.
     * @param s error's message
     */
    public void displayError(String s) {
        sequentialTransition.stop();
        errorText.setText(s);
        sequentialTransition.play();
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

}
