package ch.astina.covi.tracker;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "app")
public class AppProperties
{
    private String secret;

    private URI redirectUrlSuccess;

    private URI redirectUrlError;

    public String getSecret()
    {
        return secret;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
    }

    public URI getRedirectUrlSuccess()
    {
        return redirectUrlSuccess;
    }

    public void setRedirectUrlSuccess(URI redirectUrlSuccess)
    {
        this.redirectUrlSuccess = redirectUrlSuccess;
    }

    public URI getRedirectUrlError()
    {
        return redirectUrlError;
    }

    public void setRedirectUrlError(URI redirectUrlError)
    {
        this.redirectUrlError = redirectUrlError;
    }
}
