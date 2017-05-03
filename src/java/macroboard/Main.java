package macroboard;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import macroboard.controls.ControlsLibrary;
import macroboard.network.NetAdapter;
import macroboard.utility.ResourcesLocator;
import macroboard.utility.StaticLibrary;


public class Main extends Application
{
    private NetAdapter netAdapter = new NetAdapter();

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Scene scene = new Scene(buildUI(), 400, 300);
        scene.getStylesheets().add(ResourcesLocator.getMainCssPath());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        netAdapter.accept();
    }

    private Parent buildUI()
    {
        Text title = new Text("Macroboard");
        title.setCache(true);
        title.setId("title");

        HBox header = new HBox(title);
        header.setId("header");
        header.setCache(true);
        header.setPadding(new Insets(12, 30, 12, 30));
        header.setEffect(StaticLibrary.buildShadowBox(8));

        Button test = new Button("Mute");
        test.setOnAction(event -> ControlsLibrary.INSTANCE.typeMuteKey());

        VBox root = new VBox(header, test);
        return root;
    }

    @Override
    public void stop() throws Exception
    {
        super.stop();

        netAdapter.stop();
    }

    public static void main(String[] args)
    {
        setupSystemProperties();
        launch(args);
    }

    private static void setupSystemProperties()
    {
        System.setProperty("jna.library.path", ResourcesLocator.getBinariesFolder() + ";build/libs/library/shared;");
    }
}
