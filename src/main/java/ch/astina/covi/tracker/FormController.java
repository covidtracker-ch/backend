package ch.astina.covi.tracker;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FormController
{
    private final JdbcTemplate db;

    public FormController(JdbcTemplate db)
    {
        this.db = db;
    }

    @PostMapping("/save")
    public ResponseEntity<Void> handle(@RequestBody FormRequest data)
    {
        // TODO validate

        db.update("insert into covid_submission (id, age) values (null, ?)",
                data.age);
        // TODO all fields

        return ResponseEntity.accepted().build();
    }
}
