package ch.astina.covi.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Submission
{
    private final FormRequest data;

    private final SubmissionMetadata meta;

    public Submission(@JsonProperty("data") FormRequest data,
                      @JsonProperty("meta") SubmissionMetadata meta)
    {
        this.data = data;
        this.meta = meta;
    }

    public FormRequest getData()
    {
        return data;
    }

    public SubmissionMetadata getMeta()
    {
        return meta;
    }

    @Override
    public String toString()
    {
        return "Submission{" +
                "data=" + data +
                ", meta=" + meta +
                '}';
    }
}
