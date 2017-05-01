package macroboard.network;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 */
public class TcpService extends Service
{
    @Override
    protected Task createTask()
    {
        return new TcpConnection();
    }
}
