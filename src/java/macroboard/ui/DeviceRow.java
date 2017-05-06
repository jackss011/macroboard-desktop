package macroboard.ui;

import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import macroboard.network.DeviceInfo;
import macroboard.utility.StaticLibrary;


/**
 *
 */
@SuppressWarnings("FieldCanBeLocal")
public class DeviceRow extends StackPane
{
    private static final String ID_ROW = "device-row";
    private static final String ID_TEXTS = "device-texts";
    private static final String ID_NAME = "device-name";
    private static final String ID_ADDRESS = "device-address";

    private static final String CLASS_CONNECTED = "connected";
    private static final String CLASS_CONNECTING = "connecting";
    private static final String CLASS_NOT_CONNECTED = "not-connected";

    private static final float STATUS_WIDTH = 7.f;

    private Pane root;
    private Pane textsBox;
    private Text deviceName;
    private Text deviceAddress;
    private Rectangle statusBar;

    private DeviceInfo deviceInfo;
    private State state;


    public enum State
    {
        NOT_CONNECTED,
        CONNECTING,
        CONNECTED
    }

    public DeviceRow()
    {
        super();

        deviceName = new Text("Device name");
        deviceName.setId(ID_NAME);

        deviceAddress = new Text("Address");
        deviceAddress.setId(ID_ADDRESS);

        textsBox = new VBox(deviceName, deviceAddress);
        textsBox.setId(ID_TEXTS);

        VBox divBox = new VBox(textsBox);
        HBox.setHgrow(divBox, Priority.ALWAYS);
        StaticLibrary.addDivider(divBox);

        statusBar = new Rectangle();
        statusBar.setWidth(STATUS_WIDTH);

        root = new HBox(statusBar, divBox);
        root.heightProperty().addListener((o, old, crt) -> statusBar.setHeight(root.getHeight()));
        getChildren().add(root);

        setId(ID_ROW);
        setState(State.NOT_CONNECTED);
    }

    private void updateUI()
    {
        statusBar.getStyleClass().clear();

        if(deviceInfo != null)
        {
            deviceName.setText(deviceInfo.name);
            deviceAddress.setText(deviceInfo.address);
        }
        else
        {
            deviceAddress.setText("");

            switch (state)
            {
                case NOT_CONNECTED:
                    deviceName.setText("Ready to connect");
                    break;

                case CONNECTING:
                    deviceName.setText("Looking for devices...");
                    break;

                case CONNECTED:
                    //throw new AssertionError("Connected to null device");
            }
        }

        switch (state)
        {
            case NOT_CONNECTED:
                statusBar.getStyleClass().add(CLASS_NOT_CONNECTED);
                break;

            case CONNECTING:
                statusBar.getStyleClass().add(CLASS_CONNECTING);
                break;

            case CONNECTED:
                statusBar.getStyleClass().add(CLASS_CONNECTED);
                break;
        }
    }

    public void setDeviceInfo(DeviceInfo deviceInfo)
    {
        this.deviceInfo = deviceInfo;
        updateUI();
    }

    public DeviceInfo getDeviceInfo()
    {
        return deviceInfo;
    }

    public void setState(State newState)
    {
        this.state = newState;
        updateUI();
    }

    public State getState()
    {
        return state;
    }
}
