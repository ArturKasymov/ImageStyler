package Utils.controls;

import Utils.Constants;
import app.AppManager;
import com.sun.javafx.css.converters.SizeConverter;
import javafx.beans.property.DoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RingAnimation extends Control {

    public RingAnimation() {
        this.getStylesheets().add(AppManager.class.getResource( Constants.separator+"stylesheets"+Constants.separator+"ringAnimation.css")
                .toExternalForm());
        this.getStyleClass().add("ringindicator");
    }

    public final void setInnerCircleRadius(int value) {
        innerCircleRadiusProperty().set(value);
    }

    public final DoubleProperty innerCircleRadiusProperty() {
        return innerCircleRadius;
    }

    public final double getInnerCircleRadius() {
        return innerCircleRadiusProperty().get();
    }

    private DoubleProperty innerCircleRadius = new StyleableDoubleProperty(60) {
        @Override
        public Object getBean() {
            return RingAnimation.this;
        }

        @Override
        public String getName() {
            return "innerCircleRadius";
        }

        @Override
        public CssMetaData<? extends Styleable, Number> getCssMetaData() {
            return StyleableProperties.INNER_CIRCLE_RADIUS;
        }
    };

    private static class StyleableProperties {
        private static CssMetaData<RingAnimation, Number> INNER_CIRCLE_RADIUS = new CssMetaData<RingAnimation, Number>(
                "-fx-inner-radius", SizeConverter.getInstance(), 60) {

            @Override
            public boolean isSettable(RingAnimation styleable) {
                return styleable.innerCircleRadiusProperty() == null || !styleable.innerCircleRadiusProperty().isBound();
            }

            @Override
            public StyleableProperty<Number> getStyleableProperty(RingAnimation styleable) {
                return (StyleableProperty<Number>) styleable.innerCircleRadiusProperty();
            }
        };

        private static CssMetaData<RingAnimation, Number> RING_WIDTH = new CssMetaData<RingAnimation, Number>(
                "-fx-ring-width", SizeConverter.getInstance(), 22) {

            @Override
            public boolean isSettable(RingAnimation styleable) {
                return styleable.ringWidth == null || !styleable.ringWidth.isBound();
            }

            @Override
            public StyleableProperty<Number> getStyleableProperty(RingAnimation styleable) {
                return (StyleableProperty<Number>) styleable.ringWidth;
            }
        };

        public static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Control.getClassCssMetaData());
            //styleables.addAll(RingAnimation.getClassCssMetaData());
            styleables.add(INNER_CIRCLE_RADIUS);
            styleables.add(RING_WIDTH);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new RingAnimationSkin(this);
    }

    public final void setRingWidth(int value) {
        ringWidth.set(value);
    }

    public final DoubleProperty ringWidthProperty() {
        return ringWidth;
    }

    public final double getRingWidth() {
        return ringWidthProperty().get();
    }

    private DoubleProperty ringWidth = new StyleableDoubleProperty(22) {
        @Override
        public Object getBean() {
            return RingAnimation.this;
        }

        @Override
        public String getName() {
            return "ringWidth";
        }

        @Override
        public CssMetaData<RingAnimation, Number> getCssMetaData() {
            return StyleableProperties.RING_WIDTH;
        }
    };
}