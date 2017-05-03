package macroboard.network;


/**
 *
 */
public class NetAdapter
{
    private BeaconService beaconService = new BeaconService();
    private UdpReceiver udpReceiver = new UdpReceiver();
    private TcpService tcpService = new TcpService();

    private State networkState = State.IDLE;

    private OnNetworkEventListener networkEventListener;

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

    public void accept()
    {
        setNetworkState(State.CONNECTING);

        beaconService.start();
        tcpService.start();
    }

    public void close()
    {
        beaconService.cancel();
        tcpService.cancel();
        udpReceiver.cancel();

        setNetworkState(State.IDLE);
    }

    public Object getConnectedDevice()
    {
        return null;
    }

    public void setNetworkEventListener(OnNetworkEventListener networkEventListener)
    {
        this.networkEventListener = networkEventListener;

        if(this.networkEventListener != null)
            notifyNetworkState();
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
        if(newState != networkState)
        {
            networkState = newState;
            notifyNetworkState();
        }
    }
}
