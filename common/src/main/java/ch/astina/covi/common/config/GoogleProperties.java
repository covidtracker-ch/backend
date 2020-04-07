package ch.astina.covi.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "google")
public class GoogleProperties
{
    private String projectId;

    private String pubsubTopic;

    private String pubsubSubscription;

    public String getProjectId()
    {
        return projectId;
    }

    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }

    public String getPubsubTopic()
    {
        return pubsubTopic;
    }

    public void setPubsubTopic(String pubsubTopic)
    {
        this.pubsubTopic = pubsubTopic;
    }

    public String getPubsubSubscription()
    {
        return pubsubSubscription;
    }

    public void setPubsubSubscription(String pubsubSubscription)
    {
        this.pubsubSubscription = pubsubSubscription;
    }
}
