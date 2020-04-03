package ch.astina.covi.test;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Configuration
public class PostgresDbInitializer implements InitializingBean
{
    private final JdbcTemplate db;

    public PostgresDbInitializer(JdbcTemplate db)
    {
        this.db = db;
    }

    @Override
    public void afterPropertiesSet()
    {
        // Load db/init.sql script and execute it

        ClassPathResource dbResource = new ClassPathResource("db/init.sql");

        try (Reader reader = new InputStreamReader(dbResource.getInputStream(), UTF_8)) {

            String sql = FileCopyUtils.copyToString(reader);

            db.execute(sql);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }
}
