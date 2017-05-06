package macroboard.utility;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 */
@SuppressWarnings("WeakerAccess")
public class StaticLibrary
{

// |==========================
// |==>  EFFECTS
// |==========================

    private static final Color SHADOW_COLOR = Color.LIGHTGRAY;
    private static final double SHADOW_OFFSET = 3.0;

    /** Build an android-like shadow which can be added to a Node */
    public static DropShadow buildShadowBox(float elevation)
    {
        DropShadow ds = new DropShadow();
        ds.setColor(SHADOW_COLOR);
        ds.setHeight(elevation);
        ds.setOffsetX(SHADOW_OFFSET);
        ds.setOffsetY(SHADOW_OFFSET);

        return ds;
    }



// |==========================
// |==>  DIVIDERS
// |==========================

    private static final float DIVIDER_SIZE = 1.f;

    /**
     *  Add a thin Rectangle to the parent.
     *  @param parent the divider is added to this node
     *  @param vertical whenever the divider is vertical or not, if the parent is a HBox/VBox the orientation
     *                  can be auto-determined: use addDivider(parent)
     */
    public static void addDivider(Pane parent, boolean vertical)
    {
        if(parent == null ) throw new AssertionError("Divider null parent");

        Rectangle r = new Rectangle(DIVIDER_SIZE, DIVIDER_SIZE);
        r.getStyleClass().add("divider");
        ReadOnlyDoubleProperty p = vertical ? parent.heightProperty() : parent.widthProperty();
        p.addListener(new ChangeListener<Number>()      //TODO: try to use lambda
        {
            private final boolean v = vertical;
            private final Rectangle rect = r;

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
                if(v) rect.setHeight(newValue.floatValue());
                else rect.setWidth(newValue.floatValue());
            }
        });

        parent.getChildren().add(r);
    }

    /**
     *  Add a thin Rectangle to a HBox or VBox parent. The orientation is adjusted based of the
     *  orientation of the parent.
     *  @param parent must be a subclass of HBox or VBox
     */
    public static void addDivider(Pane parent)
    {
        if(parent instanceof HBox)
            addDivider(parent, true);
        else if(parent instanceof VBox)
            addDivider(parent, false);
        else
            throw new AssertionError("Divider parent must be HBox or VBox");
    }
}
