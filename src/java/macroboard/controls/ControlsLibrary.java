package macroboard.controls;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 *
 */
public interface ControlsLibrary extends Library
{
    ControlsLibrary INSTANCE = (ControlsLibrary) Native.loadLibrary("mbctrls", ControlsLibrary.class);

    public void typeUnicode(short unicode);

    public void typeMuteKey();

    public void typeVolumeKey(boolean volumeUp);
}
