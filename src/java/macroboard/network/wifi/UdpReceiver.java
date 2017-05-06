package macroboard.network.wifi;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import macroboard.settings.StaticSettings;
import macroboard.utility.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;


/**
 * Service listening for UDP packets.
 */
public class UdpReceiver extends Service   //TODO: check for errors
{
    @Override
    protected Task createTask()
    {
        return new Task()
        {
            private DatagramSocket socket;

            @Override
            protected Object call() throws Exception
            {
                Log.d("Start UDP service");

                try
                {
                    socket = new DatagramSocket(StaticSettings.NET_PORT);

                    while(true)
                    {
                        if(isCancelled()) break;

                        byte buff[] = new byte[256];
                        DatagramPacket packet = new DatagramPacket(buff, buff.length);
                        socket.receive(packet);
                        String data = new String(
                                packet.getData(), packet.getOffset(),
                                packet.getLength(), StandardCharsets.UTF_8);

                        if (isCancelled()) break;

                        Log.d(data);    //TODO: check if it is our connected address
                    }
                }
                catch (Exception e)
                {
                    if(socket != null && !socket.isClosed())
                        e.printStackTrace();
                }
                finally
                {
                    if(socket != null) socket.close();
                }

                return null;
            }

            @Override
            protected void cancelled()
            {
                super.cancelled();

                if(socket != null) socket.close();
            }
        };
    }
}
