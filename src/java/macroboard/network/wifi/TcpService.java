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
    private class TcpConnection extends Task
    {
        private Socket device;

        private boolean handshakeComplete;

        @Override
        protected Object call() throws Exception
        {
            Log.d("Start TCP server");

            //connect to a device
            try(ServerSocket serverSocket = new ServerSocket(StaticSettings.NET_PORT))
            {
                device = serverSocket.accept();
                if(!device.isConnected())
                {
                    onFailure();
                    return null;
                }

            }
            catch(SocketException e)
            {
                e.printStackTrace();
                onFailure(); return null;
            }

            //data loop
            dataLoop();

            Log.d("Stop TCP server");
            handshakeComplete = false;
            return null;
        }

        private void dataLoop() throws IOException
        {
            BufferedReader reader = StaticLibrary.makeReader(device);
            while(true)
            {
                try
                {
                    if(isCancelled()) { shutdown(); break; }

                    String data = reader.readLine();
                    if(data == null)
                    {
                        onFailure(); break;
                    }
                    else
                        handleLine(data);
                }
                catch (Exception e)
                {
                    if(device != null && !device.isConnected())
                    {
                        e.printStackTrace();
                        onFailure();
                    }

                    shutdown();
                    break;
                }
            }
        }

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

        @Override
        protected void cancelled()
        {
            super.cancelled();
            shutdown();
        }

        private void onConnected(DeviceInfo deviceInfo)
        {
            Platform.runLater(() -> TcpService.this.onTcpConnected(new DeviceInfo(deviceInfo)));
        }

        private void onFailure()
        {
            shutdown();
            Platform.runLater(TcpService.this::onTcpFailure);
        }
    }


    public interface OnTcpListener
    {
        void onTcpConnected(DeviceInfo deviceInfo);

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
