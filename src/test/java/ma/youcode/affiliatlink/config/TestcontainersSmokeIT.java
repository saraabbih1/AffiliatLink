
package ma.youcode.affiliatlink.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class TestcontainersSmokeIT {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldStartPostgresAndRunFlywayMigrations() {

        Integer result = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history",
                Integer.class
        );


        assertThat(result).isNotNull();
        assertThat(result).isGreaterThan(0);
    }
}
