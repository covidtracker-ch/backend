package ch.astina.covi.tracker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class MailingListRequest {
    public final String email;

    @JsonCreator
    public MailingListRequest(@JsonProperty("email") @NotNull String email)
    {
        this.email = email;
    }
}
