package ch.astina.covi.tracker.publisher;

import ch.astina.covi.common.model.Submission;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class GoogleSubmissionPublisher implements SubmissionPublisher, DisposableBean
{
    private final static Logger log = LoggerFactory.getLogger(GoogleSubmissionPublisher.class);

    private final Publisher publisher;

    private final ObjectMapper objectMapper;

    public GoogleSubmissionPublisher(Publisher publisher, ObjectMapper objectMapper)
    {
        this.publisher = publisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(Submission submission)
    {
        final String message;
        try {
            message = objectMapper.writeValueAsString(submission);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ByteString data = ByteString.copyFromUtf8(message);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
        ApiFuture<String> future = publisher.publish(pubsubMessage);

        ApiFutures.addCallback(
                future,
                new ApiFutureCallback<String>()
                {
                    @Override
                    public void onFailure(Throwable throwable)
                    {
                        log.error("Error publishing submission message: " + message, throwable);
                    }

                    @Override
                    public void onSuccess(String messageId)
                    {
                        log.info("Published submission message with ID: " + messageId);
                    }
                },
                MoreExecutors.directExecutor());
    }

    @Override
    public void destroy() throws Exception
    {
        if (publisher != null) {
            publisher.shutdown();
            publisher.awaitTermination(1, TimeUnit.MINUTES);
        }
    }
}
