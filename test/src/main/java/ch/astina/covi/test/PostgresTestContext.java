package ch.astina.covi.test;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@ContextConfiguration(initializers = PostgresContainerInitializer.class)
@Import({
        PostgresDbInitializer.class
})
public @interface PostgresTestContext
{
}
