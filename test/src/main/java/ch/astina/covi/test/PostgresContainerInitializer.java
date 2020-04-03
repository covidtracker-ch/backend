package ch.astina.covi.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;

public class PostgresContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>
{
    private final static Logger log = LoggerFactory.getLogger(PostgresContainerInitializer.class);

    private static final String ENV_DB_HOST = "DB_HOST";
    private static final String ENV_DB_PORT = "DB_PORT";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext)
    {
        GenericContainer postgres = new GenericContainer("postgres")
                .withEnv("POSTGRES_PASSWORD", "db")
                .withExposedPorts(5432);

        postgres.start();

        String postgresAddr = postgres.getContainerIpAddress();
        Integer postgresPort = postgres.getMappedPort(5432);

        log.info("Started PostgreSQL container on {}:{}", postgresAddr, postgresPort);

        TestPropertyValues
                .of(ENV_DB_HOST + "=" + postgresAddr, ENV_DB_PORT + "=" + postgresPort)
                .applyTo(applicationContext);
    }
}
