package ch.astina.covi.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class SubmissionMetadata
{
    private final ZonedDateTime createdAt;

    /**
     * Anonymized IP address
     */
    private final String ipAnon;

    /**
     * Hashed IP address
     */
    private final String ipHash;

    /**
     * Hashed user agent
     */
    private final String uaHash;

    public SubmissionMetadata(@JsonProperty("createdAt") ZonedDateTime createdAt,
                              @JsonProperty("ipAnon") String ipAnon,
                              @JsonProperty("ipHash") String ipHash,
                              @JsonProperty("uaHash") String uaHash)
    {
        this.createdAt = createdAt;
        this.ipAnon = ipAnon;
        this.ipHash = ipHash;
        this.uaHash = uaHash;
    }

    public ZonedDateTime getCreatedAt()
    {
        return createdAt;
    }

    public String getIpAnon()
    {
        return ipAnon;
    }

    public String getIpHash()
    {
        return ipHash;
    }

    public String getUaHash()
    {
        return uaHash;
    }
}
