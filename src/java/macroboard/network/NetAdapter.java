package macroboard.network;


import macroboard.utility.Log;

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
    }


    public NetAdapter(OnNetworkEventListener networkEventListener)
    {
        this.networkEventListener = networkEventListener;
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

        if(this.networkEventListener != null)
            notifyNetworkState();
    }

    public State getNetworkState()
    {
        return networkState;
    }

    private void reset()
    {
        connectedDevice = null;

        beaconService.cancel();
        tcpService.cancel();
        udpReceiver.cancel();
    }

    private void notifyNetworkState()
    {
        if(networkEventListener != null)
        {
            if(networkState == State.FAILURE)
                networkEventListener.onNetworkFailure();
            else
                networkEventListener.onNetworkStateChanged(networkState);
        }
    }

    private void setNetworkState(State newState)
    {
        Log.d("New state: " + newState.name());

        if(newState != networkState)
        {
            networkState = newState;
            notifyNetworkState();
        }
    }

    @Override
    public void onTcpConnected(DeviceInfo deviceInfo)
    {
        beaconService.cancel();
        udpReceiver.start();

        connectedDevice = deviceInfo;
        setNetworkState(State.CONNECTED);
    }

    @Override
    public void onTcpFailure()
    {
        reset();
        setNetworkState(State.FAILURE);
    }
}
