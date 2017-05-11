package macroboard.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.text.Text;
import javafx.util.Duration;
import macroboard.utility.Log;


/**
 *
 */
public class AnimatedText extends Text
{
    private static final int BLINK_DURATION = 800;

    private Timeline blinkTimeline;

    private boolean blinkingRight;

    public AnimatedText()
    {
        super();

        blinkTimeline = new Timeline(new KeyFrame(Duration.millis(BLINK_DURATION), event -> blink()));
        blinkTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void startBlink()
    {
        blinkTimeline.playFromStart();
    }

    public void stopBlink()
    {
        blinkTimeline.stop();
    }

    private void blink()
    {
        int dotsNum = getDotsNum();

        if(dotsNum == 0)
        {
            blinkingRight = true;
            modifyDots(true);
        }
        else if(dotsNum >= 3)
        {
            blinkingRight = false;
            modifyDots(false);
        }
        else
            modifyDots(blinkingRight);
    }

    private int getDotsNum()
    {
        String text = getText();
        int dotsNum = 0;

        for(int i = text.length() - 1; i >= 0; i--)
        {
            if(text.charAt(i) != '.')
                break;
            else
                dotsNum++;
        }

        return dotsNum;
    }

    private void modifyDots(boolean add)
    {
        String t = getText();
        setText(add ? t + "." : t.substring(0, t.length() - 1));
    }
}
