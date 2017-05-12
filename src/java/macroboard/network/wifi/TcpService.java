package macroboard.network.wifi;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import macroboard.network.DeviceInfo;
import macroboard.network.Packager;
import macroboard.settings.StaticSettings;
import macroboard.utility.Log;
import macroboard.utility.StaticLibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;


/**
 *  Manages a TCP connection
 */
public class TcpService extends Service
{
    /** TCP connection Task */
    private class TcpConnection extends Task
    {
        private Socket device;

        private boolean handshakeComplete;

        @Override
        protected Object call() throws Exception
        {
            Log.d("Start TCP server");

            try
            {
                if(connect())
                    dataLoop();
                else
                    onFailure();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                onFailure();    // call in main loop
            }

            handshakeComplete = false;

            Log.d("Stop TCP server");
            return null;
        }

        /** Attempt to connect a valid socket */
        private boolean connect() throws IOException
        {
            try(ServerSocket serverSocket = new ServerSocket(StaticSettings.NET_PORT))
            {
                device = serverSocket.accept();
                return device.isConnected();
            }
        }

        /** Loop until an error occur or the task is cancelled */
        private void dataLoop() throws IOException
        {
            BufferedReader reader = StaticLibrary.makeReader(device);
            while(true)
            {
                if(isCancelled()) { shutdown(); break; }

                String data = reader.readLine();
                if(data != null)
                    handleLine(data);
                else
                    { onFailure(); break; } //input stream closed
            }
        }

        /** Manage a line of data read from the network */
        private void handleLine(String data) throws IOException
        {
            if(handshakeComplete)
            {
                Log.d(data);
            }
            else
            {
                Packager.HandShake hs = Packager.unpackHandShake(data);
                if(hs != null)
                {
                    //respond to handshake
                    PrintWriter writer = StaticLibrary.makeWriter(device);
                    writer.println(Packager.packHandShake());
                    handshakeComplete = true;

                    onConnected(new DeviceInfo(hs.deviceName, device.getInetAddress().getHostAddress()));
                }
                else
                    Log.e("Error during handshake");
            }
        }

        /** Close the socket */
        private void shutdown()
        {
            try
            {
                if(device != null) device.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                onFailure();
            }

            handshakeComplete = false;
        }

        /** Called when a Socket is connected */
        private void onConnected(DeviceInfo deviceInfo)
        {
            Platform.runLater(() -> TcpService.this.onTcpConnected(new DeviceInfo(deviceInfo)));
        }

        /** Called when a net error occur */
        private void onFailure()
        {
            shutdown();
            Platform.runLater(TcpService.this::onTcpFailure);
        }

        @Override
        protected void cancelled()
        {
            super.cancelled();
            shutdown();
        }
    }
//end


    /** Listener for TCP events */
    public interface OnTcpListener
    {
        /** Called when a socket is connected on FX thread */
        void onTcpConnected(DeviceInfo deviceInfo);

        /** Called when a failure occur on FX thread */
        void onTcpFailure();
    }



    private OnTcpListener tcpListener;


    public TcpService(OnTcpListener tcpListener)
    {
        this.tcpListener = tcpListener;
    }


    @Override
    protected Task createTask()
    {
        return new TcpConnection();
    }

    void setTcpListener(OnTcpListener tcpListener)
    {
        this.tcpListener = tcpListener;
    }

    private void onTcpConnected(DeviceInfo deviceInfo)
    {
        if(tcpListener != null) tcpListener.onTcpConnected(deviceInfo);
    }

    private void onTcpFailure()
    {
        if(tcpListener != null) tcpListener.onTcpFailure();
    }
}
