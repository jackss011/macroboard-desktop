package macroboard.utility;

import macroboard.Main;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 *  Class containing static methods used resolve paths to various resources.
 */
public class ResourcesLocator
{
    /** Resolve application root package */
    private static Path getApplicationPath()
    {
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        //java_bug_fix: if it's on windows (the path contains ':') remove the first char if it's equal to '/'
        if(path.contains(":") && path.startsWith("/"))
            path = path.substring(1);
        return Paths.get(path);
    }

    /** Get an absolute path fro the binaries */
    public static String getBinariesFolder()
    {
        return getApplicationPath().getParent().resolve("bin").toString();
    }

    /** Locate the main css path folder */
    public static String getMainCssPath()
    {
        return Main.class.getResource("main.css").toExternalForm();
    }

    /** Setup system properties using System.setProperty(k,v) */
    public static void setupSystemProperties()
    {
        System.setProperty("jna.library.path", ResourcesLocator.getBinariesFolder() + ";build/libs/library/shared;");
    }
}
