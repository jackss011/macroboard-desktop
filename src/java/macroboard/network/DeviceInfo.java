package macroboard.network;

/**
 *
 */
public class DeviceInfo
{
    public String name;
    public String address;

    public DeviceInfo(String name, String address)
    {
        this.name = name;
        this.address = address;
    }

    public DeviceInfo(DeviceInfo other)
    {
        name = other.name;
        address = other.address;
    }
}
