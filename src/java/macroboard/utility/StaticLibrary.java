package macroboard.utility;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 *
 */
public class StaticLibrary
{
    private static final Color SHADOW_COLOR = Color.LIGHTGRAY;
    private static final double SHADOW_OFFSET = 3.0;

    public static DropShadow buildShadowBox(float elevation)
    {
        DropShadow ds = new DropShadow();
        ds.setColor(SHADOW_COLOR);
        ds.setHeight(elevation);
        ds.setOffsetX(SHADOW_OFFSET);
        ds.setOffsetY(SHADOW_OFFSET);

        return ds;
    }
}
