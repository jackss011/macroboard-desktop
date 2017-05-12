package macroboard.network;

/**
 *
 */
public class Packager
{
    private final static String HD_RESPONSE = "MB_RESPONSE";
    private final static String HD_REQUEST = "MB_REQUEST";

    public static String packResponseData()
    {
        return HD_RESPONSE;
    }

    public static boolean validateBeaconRequest(String request)
    {
        return request.equals(HD_REQUEST);
    }
}
