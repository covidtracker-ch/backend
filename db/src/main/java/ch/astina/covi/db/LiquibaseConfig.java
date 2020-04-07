package ch.astina.covi.db;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:db/liquibase.properties")
public class LiquibaseConfig
{
}
