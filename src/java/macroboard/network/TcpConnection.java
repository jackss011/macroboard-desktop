package macroboard.network;

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
 *
 */
public class TcpConnection extends Task
{
    private Socket device;

    @Override
    protected Object call() throws Exception
    {
        try(ServerSocket serverSocket = new ServerSocket(StaticSettings.NET_PORT))
        {
            Log.d("Starting tcp server");
            
            device = serverSocket.accept();

            if(device.isConnected())
                Log.d("Device connected");
        }
        catch(SocketException e)
        {
            e.printStackTrace();
        }

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(device.getInputStream(), StandardCharsets.UTF_8));

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
                    Log.d("Data: " + data);
                else
                    break;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                shutdown();
                break;
            }
        }

        Log.d("Stopping tcp server");
        return null;
    }

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
}
