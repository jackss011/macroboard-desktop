package macroboard.network;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import macroboard.settings.StaticSettings;
import macroboard.utility.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 *  Service responsible to broadcast UDP packets in order to be found by other devices.
 */
class BeaconService extends Service
{
    private class BeaconTask extends Task
    {
        private MulticastSocket receiver;

        @Override
        protected Void call() throws Exception
        {
            System.out.println("Start BEACON service");

            try (DatagramSocket responder = new DatagramSocket())
            {
                receiver = new MulticastSocket(StaticSettings.NET_PORT);
                receiver.joinGroup(InetAddress.getByName(StaticSettings.MULTICAST_ADDRESS));

                while (true)
                {
                    byte[] buff = new byte[256];
                    DatagramPacket packet = new DatagramPacket(buff, buff.length);
                    receiver.receive(packet);

                    if (isCancelled()) break;

                    String requestData = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);

                    if (true) //TODO: check data packet data here
                    {
                        InetAddress senderAddress = packet.getAddress();
                        Log.d("Received from: " + senderAddress.getHostAddress());

                        final String responseData = "response";

                        DatagramPacket responsePacket = new DatagramPacket(
                                responseData.getBytes(StandardCharsets.UTF_8),
                                responseData.length(),
                                senderAddress,
                                StaticSettings.NET_PORT);

                        responder.send(responsePacket);
                    }
                }
            }
            catch (Exception e)
            {
                if(receiver!= null && !receiver.isClosed())
                    e.printStackTrace();
            }
            finally
            {
                if(receiver != null) receiver.close();
            }

            return null;
        }

        @Override
        protected void cancelled()
        {
            if(receiver!= null) receiver.close();
        }
    }

    @Override
    protected Task createTask()
    {
        return new BeaconTask();
    }
}
