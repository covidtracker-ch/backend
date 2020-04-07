package ch.astina.covi.tracker;

import ch.astina.covi.common.model.FormRequest;
import ch.astina.covi.common.model.Submission;
import ch.astina.covi.common.model.SubmissionMetadata;
import ch.astina.covi.tracker.publisher.SubmissionPublisher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;

@Component
public class SubmissionHandler
{
    private final SubmissionPublisher publisher;

    private final Utils utils;

    public SubmissionHandler(SubmissionPublisher publisher, Utils utils)
    {
        this.publisher = publisher;
        this.utils = utils;
    }

    public void handle(FormRequest formData, HttpServletRequest request)
    {
        SubmissionMetadata metadata = new SubmissionMetadata(
                ZonedDateTime.now(),
                utils.anonymizeIp(request.getRemoteAddr()),
                utils.hashIp(request.getRemoteAddr()),
                utils.hashUserAgent(request)
        );

        Submission submission = new Submission(formData, metadata);

        publisher.publish(submission);
    }
}
