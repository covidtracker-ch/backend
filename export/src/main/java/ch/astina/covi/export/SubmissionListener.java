package ch.astina.covi.export;

import ch.astina.covi.common.config.GoogleProperties;
import ch.astina.covi.common.model.Submission;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class SubmissionListener implements ApplicationListener<ApplicationReadyEvent>
{
    private final static Logger log = LoggerFactory.getLogger(SubmissionListener.class);

    private final SubmissionHandler handler;

    private final ObjectMapper objectMapper;

    private final GoogleProperties props;

    public SubmissionListener(SubmissionHandler handler, ObjectMapper objectMapper, GoogleProperties props)
    {
        this.handler = handler;
        this.objectMapper = objectMapper;
        this.props = props;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event)
    {
        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(props.getProjectId(), props.getPubsubSubscription());

        MessageReceiver receiver = (message, consumer) -> {

            try {
                Submission submission = objectMapper.readValue(message.getData().toStringUtf8(), Submission.class);
                handler.handle(submission);
                consumer.ack();
            } catch (JsonProcessingException e) {
                log.error("Error while handling submission message :" + message.getMessageId(), e);
                consumer.nack();
            }
        };

        Subscriber subscriber = null;
        try {
            subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
            subscriber.startAsync().awaitRunning();
            subscriber.awaitTerminated();
        } finally {
            // Stop receiving messages
            if (subscriber != null) {
                subscriber.stopAsync();
            }
        }
    }
}
