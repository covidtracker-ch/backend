package ch.astina.covi.export;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
public class ExportApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(ExportApplication.class, args);
    }

    @Configuration
    public static class SecurityConfig extends WebSecurityConfigurerAdapter
    {
        @Override
        public void configure(WebSecurity web)
        {
            web.ignoring().antMatchers("/count");
        }
    }
}
