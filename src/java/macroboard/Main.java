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
import macroboard.network.DeviceInfo;
import macroboard.network.NetAdapter;
import macroboard.ui.DeviceRow;
import macroboard.utility.Log;
import macroboard.utility.ResourcesLocator;
import macroboard.utility.StaticLibrary;


public class Main extends Application implements NetAdapter.OnNetworkEventListener
{
    private NetAdapter netAdapter;
    private DeviceRow deviceRow;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Scene scene = new Scene(buildUI(), 400, 300);
        scene.getStylesheets().add(ResourcesLocator.getMainCssPath());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();


        netAdapter = new NetAdapter(this);
        netAdapter.accept();
    }

    private Parent buildUI()
    {
        Text title = new Text("Macroboard");
        title.setCache(true);
        title.setId("title");

        HBox header = new HBox(title);  //TODO: add to main.sass
        header.setId("header");
        header.setCache(true);
        header.setPadding(new Insets(12, 30, 12, 30));
        header.setEffect(StaticLibrary.buildShadowBox(8));

        Button test = new Button("Mute");
        test.setOnAction(event -> ControlsLibrary.INSTANCE.typeMuteKey());

        deviceRow = new DeviceRow();

        return new VBox(header, deviceRow);
    }

    @Override
    public void stop() throws Exception
    {
        super.stop();

        netAdapter.stop();
    }

    @Override
    public void onNetworkStateChanged(NetAdapter.State newState)
    {
        Log.d("New state: " + newState.name());

        switch (newState)
        {
            case IDLE:
                deviceRow.setState(DeviceRow.State.NOT_CONNECTED);
                break;

            case CONNECTING:
                deviceRow.setState(DeviceRow.State.CONNECTING);
                break;

            case CONNECTED:
                deviceRow.setState(DeviceRow.State.CONNECTED);
                break;

            case FAILURE:
                deviceRow.setState(DeviceRow.State.NOT_CONNECTED);
                break;
        }
    }

    @Override
    public void onNetworkFailure()
    {
        Log.e("Net failure");
    }

    @Override
    public void onDeviceChange(DeviceInfo deviceInfo)
    {
        deviceRow.setDeviceInfo(deviceInfo);
    }

    public static void main(String[] args)
    {
        ResourcesLocator.setupSystemProperties();
        launch(args);
    }
}
