package macroboard.network;

/**
 *
 */
public class Packager
{
    private final static String DIV = ";";
    private final static String SPEC = ":";

    private final static String HS_HEADER = "MB_HANDSHAKE";
    private final static String HS_NAME = "N";

    private final static String BE_RESPONSE = "MB_RESPONSE";
    private final static String BE_REQUEST = "MB_REQUEST";


// |==========================
// |==>  HANDSHAKE
// |==========================

    public static class HandShake
    {
        public String deviceName;

        private HandShake(String deviceName)
        {
            this.deviceName = deviceName;
        }
    }

    public static String packHandShake()
    {
        return HS_HEADER;
    }

    public static HandShake unpackHandShake(String handshake)
    {
        String divs[] = handshake.split(DIV);
        if(divs.length >= 2 && divs[0].equals(HS_HEADER))
        {
            String spec[] = divs[1].split(SPEC);
            if(spec.length == 2 && spec[0].equals(HS_NAME))
                return new HandShake(spec[1]);
        }

        return null;
    }


// |==========================
// |==>  BEACON
// |==========================

    public static String packResponseData()
    {
        return BE_RESPONSE;
    }

    public static boolean validateBeaconRequest(String request)
    {
        return request.equals(BE_REQUEST);
    }
}
