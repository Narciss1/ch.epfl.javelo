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
    private final SequentialTransition sequentialTransition;

    /**
     * duration of the first fade transition.
     */
    private final static int FIRST_FADE_DURATION = 200;
    /**
     * duration of the pause transition.
     */
    private final static int PAUSE_DURATION = 2000;
    /**
     * duration of the last fade transition.
     */
    private final static int LAST_FADE_DURATION = 500;
    /**
     * the start opacity of the error message.
     */
    private final static double START_OPACITY_MESSAGE = 0;
    /**
     * the last opacity of the error message.
     */
    private final static double LAST_OPACITY_MESSAGE = 0.8;

    /**
     * Constructor
     */
    public ErrorManager() {
        errorPane = new VBox();
        errorPane.getStylesheets().add("error.css");
        errorPane.setMouseTransparent(true);
        errorText = new Text();
        errorPane.getChildren().add(errorText);
        FadeTransition firstFdeTransition = new FadeTransition(new Duration(FIRST_FADE_DURATION),
                errorPane);
        firstFdeTransition.setFromValue(START_OPACITY_MESSAGE);
        firstFdeTransition.setToValue(LAST_OPACITY_MESSAGE);
        PauseTransition pauseTransition = new PauseTransition(new Duration(PAUSE_DURATION));
        FadeTransition lastFadeTransition = new FadeTransition(new Duration(LAST_FADE_DURATION),
                errorPane);
        lastFadeTransition.setFromValue(LAST_OPACITY_MESSAGE);
        lastFadeTransition.setToValue(START_OPACITY_MESSAGE);
        sequentialTransition = new SequentialTransition(firstFdeTransition,
                pauseTransition, lastFadeTransition);
    }

    /**
     * Allow us to obtain the plan on which the errors' message appears
     * @return the plan on which the errors' message appears
     */
    public Pane pane() {
        return errorPane;
    }

    /**
     * makes the error's message briefly appears on the pane with an error sound.
     * @param s error's message
     */
    public void displayError(String s) {
        errorText.setText(s);
        sequentialTransition.playFromStart();
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

}
