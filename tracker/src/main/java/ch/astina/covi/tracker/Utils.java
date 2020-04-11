package ch.astina.covi.tracker;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.net.InetAddresses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

import static com.google.common.base.Charsets.UTF_8;

@Component
public class Utils
{
    private final static Logger log = LoggerFactory.getLogger(Utils.class);

    private final HashFunction hashFunction;

    public Utils(AppProperties properties)
    {
        Assert.notNull(properties.getSecret(), "${app.secret} cannot be null");
        hashFunction = Hashing.hmacSha256(new SecretKeySpec(properties.getSecret().getBytes(UTF_8), "HmacSHA256"));
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

    public static ResponseEntity<Void> redirect(URI formRedirectUrlSuccess)
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(formRedirectUrlSuccess);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .headers(httpHeaders)
                .build();
    }

    public static URI appendUriParam(URI oldUri, String appendQuery) throws URISyntaxException {
        String newQuery = oldUri.getQuery();

        if (newQuery == null) {
            newQuery = appendQuery;
        }
        else {
            newQuery += "&" + appendQuery;
        }

        return new URI(oldUri.getScheme(), oldUri.getAuthority(),
                oldUri.getPath(), newQuery, oldUri.getFragment());
    }

    public static URI prependLanguageCode(URI oldUri, FormRequest.UserLanguage userLang) {
        // use the submitted language to construct a response URL w/an embedded language
        // (if it's german there's no prefix, so we return the URI unmodified)
        if (userLang != FormRequest.UserLanguage.de) {
            // reconstruct the URL with a new path, but everything else the same
            try {
                return new URI(
                        oldUri.getScheme(),
                        oldUri.getAuthority(),
                        "/" + userLang.toString() + oldUri.getPath(),
                        oldUri.getQuery(),
                        oldUri.getFragment()
                );
            } catch (URISyntaxException e) {
                // if we munge something, just return what we had before, which will still work...
                return oldUri;
            }
        }

        return oldUri;
    }
}
