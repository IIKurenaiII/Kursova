package org.kursova.animations;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class Shake {
    private TranslateTransition tt;
    private FadeTransition fadeIn;
    private FadeTransition fadeOut;

    public Shake(Node node) {
        tt = new TranslateTransition(Duration.millis(77), node);
        tt.setFromX(0f);
        tt.setByX(10f);
        tt.setCycleCount(3);
        tt.setAutoReverse(true);

        fadeIn = new FadeTransition(Duration.millis(200), node);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeOut = new FadeTransition(Duration.millis(300), node);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
    }

    public void playAnim() {
        tt.playFromStart();
    }

    public void fadeIn() {
        fadeIn.playFromStart();
    }

    public void fadeOut() {
        fadeOut.playFromStart();
    }
}
