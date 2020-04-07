package ch.astina.covi.tracker;

import ch.astina.covi.db.LiquibaseConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@Import({
        LiquibaseConfig.class,
})
public class TrackerApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(TrackerApplication.class, args);
    }
}
