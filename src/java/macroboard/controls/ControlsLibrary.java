package macroboard.controls;

import com.sun.jna.Library;
import com.sun.jna.Native;
import macroboard.settings.StaticSettings;

/**
 *
 */
public interface ControlsLibrary extends Library
{
    ControlsLibrary INSTANCE = (ControlsLibrary) Native.loadLibrary(
            StaticSettings.NATIVE_LIBRARY_NAME,
            ControlsLibrary.class);

    public void typeUnicode(short unicode);

    public void typeMuteKey();

    public void typeVolumeKey(boolean volumeUp);
}
