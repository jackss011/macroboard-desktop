package macroboard;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import macroboard.utility.StaticLibrary;

import java.nio.file.Path;
import java.nio.file.Paths;


public class Main extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Scene scene = new Scene(buildUI(), 400, 300);
        scene.getStylesheets().add(getMainCssPath());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private Parent buildUI()
    {
        Text title = new Text("Macroboard");
        title.setCache(true);
        title.setId("app-title");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 25));

        HBox header = new HBox(title);
        header.setCache(true);
        header.setPadding(new Insets(12, 30, 12, 30));
        header.setStyle("-fx-background-color: #009688;");
        header.setEffect(StaticLibrary.buildShadowBox(8));

        VBox root = new VBox(header);
        return root;
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

    public static Path getApplicationPath()
    {
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        // java bug fix: if it's on windows (the path contains ':') remove the first char if it's equal to '/'
        if(path.contains(":") && path.startsWith("/"))
            path = path.substring(1);
        return Paths.get(path);
    }

    public static String getBinaryFolder()
    {
        return getApplicationPath().getParent().resolve("bin").toString();
    }

    public static String getMainCssPath()
    {
        return Main.class.getResource("main.css").toExternalForm();
    }
}
