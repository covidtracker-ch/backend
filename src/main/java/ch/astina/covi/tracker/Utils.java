package ch.astina.covi.tracker;

import com.google.common.net.InetAddresses;

import java.net.InetAddress;

public class Utils
{
    public static String anonymizeIp(String addr)
    {
        try {

            InetAddress inetAddress = InetAddress.getByName(addr);

            final byte[] address = inetAddress.getAddress();
            address[address.length - 1] = 0;
            if (address.length > 4) { // ipv6
                for (int i = 6; i < address.length; i++) {
                    address[i] = 0;
                }
            }

            String result = InetAddresses.toAddrString(InetAddress.getByAddress(address));
            if (address.length == 4) {
                result = result.replaceFirst("\\.0$", "._");
            }

            return result;

        } catch (final Exception e) {
            return "???.???.???.???";
        }
    }
}
