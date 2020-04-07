package ch.astina.covi.tracker;

import ch.astina.covi.common.config.GoogleProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(GoogleProperties.class)
public class TrackerApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(TrackerApplication.class, args);
    }
}
