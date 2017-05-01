package macroboard;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import macroboard.controls.ControlsLibrary;
import macroboard.network.Beacon;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        StackPane root = new StackPane();
        primaryStage.setTitle(getBinaryFolder());
        primaryStage.setScene(new Scene(root, 1000, 275));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception
    {
        super.stop();
    }

    public static void main(String[] args)
    {
        setupSystemProperties();
        launch(args);
    }

    private static void setupSystemProperties()
    {
        System.setProperty("jna.library.path", "build/native;" + getBinaryFolder());
    }

    private static String getBinaryFolder()
    {
        Path p = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation()
                .getPath().substring(1));       //TODO: fix for other platforms
        return p.getParent().resolve("bin").toString();
    }
}
