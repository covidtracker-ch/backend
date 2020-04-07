package ch.astina.covi.tracker.publisher;

import ch.astina.covi.common.model.Submission;

public interface SubmissionPublisher
{
    void publish(Submission submission);
}
