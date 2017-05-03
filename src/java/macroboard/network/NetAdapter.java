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

    public void stop()
    {
        reset();
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

    private void reset()
    {
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
    public void onTcpConnected()
    {
        beaconService.cancel();
        udpReceiver.start();
        setNetworkState(State.CONNECTED);
    }

    @Override
    public void onTcpFailure()
    {
        reset();
        setNetworkState(State.FAILURE);
    }
}
