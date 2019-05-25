package Utils.controls;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class RingAnimationSkin implements Skin<RingAnimation> {

    private final RingAnimation indicator;
    private final Circle innerCircle = new Circle();
    private final Circle outerCircle = new Circle();
    private final StackPane container = new StackPane();
    private final Arc fillerArc = new Arc();
    private final RotateTransition transition = new RotateTransition(Duration.millis(2000), fillerArc);

    public RingAnimationSkin(final RingAnimation indicator) {
        this.indicator = indicator;
        initContainer(indicator);
        initFillerArc();
        container.widthProperty().addListener((o, oldVal, newVal) -> {
            fillerArc.setCenterX(newVal.intValue() / 2);
        });
        container.heightProperty().addListener((o, oldVal, newVal) -> {
            fillerArc.setCenterY(newVal.intValue() / 2);
        });
        innerCircle.getStyleClass().add("ringindicator-inner-circle");
        outerCircle.getStyleClass().add("ringindicator-outer-circle-secondary");
        updateRadii();

        this.indicator.ringWidthProperty().addListener((o, oldVal, newVal) -> {
            updateRadii();
        });
        innerCircle.strokeWidthProperty().addListener((e) -> {
            updateRadii();
        });
        innerCircle.radiusProperty().addListener((e) -> {
            updateRadii();
        });
        initTransition();
        initIndeterminate();
        indicator.visibleProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                transition.play();
            } else {
                transition.pause();
            }
        });
        container.getChildren().addAll(fillerArc, outerCircle, innerCircle);
    }

    private void initContainer(final RingAnimation indicator) {
        container.getStylesheets().addAll(indicator.getStylesheets());
        container.getStyleClass().addAll("ringindicator-container");
        container.setMaxHeight(Region.USE_PREF_SIZE);
        container.setMaxWidth(Region.USE_PREF_SIZE);
    }

    private void initFillerArc() {
        fillerArc.setManaged(false);
        fillerArc.getStyleClass().add("ringindicator-filler");
        fillerArc.setStartAngle(90);
        fillerArc.setLength(3.6);
    }

    private void updateRadii() {
        double ringWidth = indicator.getRingWidth();
        double innerCircleHalfStrokeWidth = innerCircle.getStrokeWidth() / 2;
        double innerCircleRadius= indicator.getInnerCircleRadius();
        outerCircle.setRadius(innerCircleHalfStrokeWidth + innerCircleRadius + ringWidth);
        fillerArc.setRadiusX(innerCircleHalfStrokeWidth + innerCircleRadius - 1 + ringWidth/2);
        fillerArc.setRadiusY(innerCircleHalfStrokeWidth + innerCircleRadius - 1 + ringWidth/2);
        fillerArc.setStrokeWidth(ringWidth);
        innerCircle.setRadius(innerCircleRadius);
    }

    private void initTransition() {
        transition.setAutoReverse(false);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.setDelay(Duration.ZERO);
        transition.setInterpolator(Interpolator.LINEAR);
        transition.setByAngle(360);
    }

    private void initIndeterminate() {
        fillerArc.setLength(360);
        fillerArc.getStyleClass().add("indeterminate");
        if (indicator.isVisible()) transition.play();
    }

    @Override
    public RingAnimation getSkinnable() {
        return indicator;
    }

    @Override
    public Node getNode() {
        return container;
    }

    @Override
    public void dispose() {
        transition.stop();
    }
}
