package ch.astina.covi.tracker;

import ch.astina.covi.common.config.GoogleProperties;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.pubsub.v1.ProjectTopicName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
public class GoogleConfig
{
    @Profile({"prod", "stage"})
    @Bean
    public Publisher publisher(GoogleProperties props) throws IOException
    {
        ProjectTopicName topicName = ProjectTopicName.of(props.getProjectId(), props.getPubsubTopic());

        return Publisher.newBuilder(topicName)
                .build();
    }

    @Profile("dev")
    @Bean
    public Publisher devPublisher(GoogleProperties props) throws IOException
    {
        String hostport = "localhost:8085"; //System.getenv("PUBSUB_EMULATOR_HOST");
        ManagedChannel channel = ManagedChannelBuilder.forTarget(hostport).usePlaintext().build();
        try {
            TransportChannelProvider channelProvider =
                    FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
            CredentialsProvider credentialsProvider = NoCredentialsProvider.create();

            // Set the channel and credentials provider when creating a `TopicAdminClient`.
            // Similarly for SubscriptionAdminClient
            TopicAdminClient topicClient =
                    TopicAdminClient.create(
                            TopicAdminSettings.newBuilder()
                                    .setTransportChannelProvider(channelProvider)
                                    .setCredentialsProvider(credentialsProvider)
                                    .build());

            ProjectTopicName topicName = ProjectTopicName.of(props.getProjectId(), props.getPubsubTopic());
            // Set the channel and credentials provider when creating a `Publisher`.
            // Similarly for Subscriber

            topicClient.createTopic(topicName);

            return Publisher.newBuilder(topicName)
                    .setChannelProvider(channelProvider)
                    .setCredentialsProvider(credentialsProvider)
                    .build();

        } finally {
            channel.shutdown();
        }
    }
}
