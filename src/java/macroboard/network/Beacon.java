package macroboard.network;

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
 *
 */
public class Beacon
{
    private BeaconTask beaconTask;

    private ExecutorService executorService;

    private Future taskFuture;


// |==========================
// |==>  CLASSES
// |==========================

    private class BeaconTask extends Task
    {
        @Override
        protected Void call() throws Exception
        {
            System.out.println("Running");

            try(MulticastSocket receiver = new MulticastSocket(StaticSettings.NET_PORT);
                DatagramSocket responder = new DatagramSocket())
            {
                receiver.joinGroup(InetAddress.getByName(StaticSettings.MULTICAST_ADDRESS));

                while(true)
                {
                    byte[] buff = new byte[256];
                    DatagramPacket packet = new DatagramPacket(buff, buff.length);
                    receiver.receive(packet);

                    if (Thread.interrupted()) break;

                    String requestData = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);

                    if(true) //TODO: check data packet data here
                    {
                        InetAddress senderAddress = packet.getAddress();
                        System.out.println("Received from: " + senderAddress.getHostAddress());

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
                e.printStackTrace();
            }

            return null;
        }
    }



// |==========================
// |==>  CONSTRUCTORS
// |==========================

    public Beacon()
    {
        executorService = Executors.newSingleThreadExecutor(r ->
        {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
    }



// |==========================
// |==>  METHODS
// |==========================

    public boolean isRunning()
    {
        return taskFuture != null && !taskFuture.isDone() && !taskFuture.isCancelled();
    }

    public void start()
    {
        if(!isRunning())
        {
            Log.d("Running beacon");
            beaconTask = new BeaconTask();
            taskFuture = executorService.submit(new BeaconTask());
        }
    }

    public void stop()
    {
        if(isRunning())
        {
            Log.d("Stopping beacon");
            taskFuture.cancel(true);
            taskFuture = null;
        }
    }

}
