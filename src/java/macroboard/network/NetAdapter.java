package macroboard.network;


/**
 *
 */
public class NetAdapter implements TcpService.OnTcpListener
{
    private BeaconService beaconService = new BeaconService();
    private UdpReceiver udpReceiver = new UdpReceiver();
    private TcpService tcpService = new TcpService(this);

    private State networkState = State.IDLE;

    private OnNetworkEventListener networkEventListener;

    private DeviceInfo connectedDevice;


    public enum State
    {
        IDLE,
        CONNECTING,
        CONNECTED,
        FAILURE
    }

    public interface OnNetworkEventListener
    {
        void onNetworkStateChanged(State newState);

        void onNetworkFailure();

        void onDeviceChange(DeviceInfo deviceInfo);
    }


    public NetAdapter(OnNetworkEventListener networkEventListener)
    {
        setNetworkEventListener(networkEventListener);
    }


    public void accept()
    {
        if(networkState == State.IDLE)
        {
            beaconService.start();
            tcpService.start();

            setNetworkState(State.CONNECTING);
        }
    }

    public void stop()
    {
        reset();
        setNetworkState(State.IDLE);
    }

    public void restart()
    {
        reset();
        accept();
    }

    public DeviceInfo getConnectedDevice()
    {
        return connectedDevice;
    }

    public void setNetworkEventListener(OnNetworkEventListener networkEventListener)
    {
        this.networkEventListener = networkEventListener;

        notifyNetworkState();
        notifyDeviceChange();
    }

    public State getNetworkState()
    {
        return networkState;
    }

    private void reset()
    {
        setConnectedDevice(null);

        beaconService.cancel();
        tcpService.cancel();
        udpReceiver.cancel();
    }

    private void setNetworkState(State newState)
    {
        //Log.d("New state: " + newState.name());

        if(newState != networkState)
        {
            networkState = newState;
            notifyNetworkState();
        }
    }

    private void notifyNetworkState()
    {
        if(networkEventListener != null)
        {
            if(networkState == State.FAILURE)
                networkEventListener.onNetworkFailure();

            networkEventListener.onNetworkStateChanged(networkState);
        }
    }

    private void setConnectedDevice(DeviceInfo info)
    {
        this.connectedDevice = info;
        notifyDeviceChange();
    }

    private void notifyDeviceChange()
    {
        if(networkEventListener != null)
            networkEventListener.onDeviceChange(getConnectedDevice());
    }

    @Override
    public void onTcpConnected(DeviceInfo deviceInfo)
    {
        beaconService.cancel();
        udpReceiver.start();

        setConnectedDevice(deviceInfo);
        setNetworkState(State.CONNECTED);
    }

    @Override
    public void onTcpFailure()
    {
        reset();
        setNetworkState(State.FAILURE);
    }
}
