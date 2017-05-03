package macroboard.network;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import macroboard.settings.StaticSettings;
import macroboard.utility.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;


/**
 *  Manages a TCP connection
 */
class TcpService extends Service
{
    private class TcpConnection extends Task
    {
        private Socket device;

        @Override
        protected Object call() throws Exception
        {
            try(ServerSocket serverSocket = new ServerSocket(StaticSettings.NET_PORT))
            {
                Log.d("Start TCP service");

                device = serverSocket.accept();

                if(device.isConnected())
                {
                    Log.d("Device connected");
                    onConnected();  //TODO: provide device info
                }
                else
                {
                    Log.e("Device connection error");
                    onFailure();
                }

            }
            catch(SocketException e)
            {
                e.printStackTrace();
                onFailure();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    device.getInputStream(),
                    StandardCharsets.UTF_8));

            while(true)
            {
                try
                {
                    if(isCancelled())
                    {
                        shutdown();
                        break;
                    }

                    String data = reader.readLine();

                    if(data != null)
                    {
                        Log.d("Data: " + data);
                    }
                    else
                    {
                        Log.e("Null data");
                        onFailure();
                        break;
                    }
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

            Log.d("Stop TCP server");
            return null;
        }

        // Close the socket
        private void shutdown()
        {
            try
            {
                if(device != null) device.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected void cancelled()
        {
            super.cancelled();
            shutdown();
        }

        private void onConnected()
        {
            Platform.runLater(() -> TcpService.this.onTcpConnected());
        }

        private void onFailure()
        {
            Platform.runLater(TcpService.this::onTcpFailure);
        }
    }

    interface OnTcpListener
    {
        void onTcpConnected();

        void onTcpFailure();
    }



    private OnTcpListener tcpListener;


    TcpService(OnTcpListener tcpListener)
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

    private void onTcpConnected()
    {
        if(tcpListener != null) tcpListener.onTcpConnected();
    }

    private void onTcpFailure()
    {
        if(tcpListener != null) tcpListener.onTcpFailure();
    }
}
