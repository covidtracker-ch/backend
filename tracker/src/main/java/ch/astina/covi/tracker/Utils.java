package ch.astina.covi.tracker;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.net.InetAddresses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

import static com.google.common.base.Charsets.UTF_8;

@Component
public class Utils
{
    private final static Logger log = LoggerFactory.getLogger(Utils.class);

    private final HashFunction hashFunction;

    public Utils(@Value("${app.secret}") String secret)
    {
        Assert.notNull(secret, "${app.secret} cannot be null");
        hashFunction = Hashing.hmacSha256(new SecretKeySpec(secret.getBytes(UTF_8), "HmacSHA256"));
    }

    public String anonymizeIp(String addr)
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

            return InetAddresses.toAddrString(InetAddress.getByAddress(address));

        } catch (final Exception e) {
            log.error("Failed to anonymize IP address", e);
            return "???.???.???.???";
        }
    }

    public String hashIp(String remoteAddr)
    {
        try {
            return hashFunction.hashString(remoteAddr, UTF_8).toString();
        } catch (final Exception e) {
            log.error("Failed to hash IP address", e);
            return "???";
        }
    }

    public String hashUserAgent(HttpServletRequest request)
    {
        try {
            return hashFunction.hashString(request.getHeader("User-Agent"), UTF_8).toString();
        } catch (final Exception e) {
            log.error("Failed to hash user agent", e);
            return "???";
        }
    }
}
