package io.github.stackpan.tasque.http;

import io.github.stackpan.tasque.TestContainersConfig;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@Import(TestContainersConfig.class)
@Sql(
        scripts = {"classpath:datasources/user.sql", "classpath:datasources/team.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        statements = {"DELETE FROM team_members", "DELETE FROM teams", "DELETE FROM users"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
@AutoConfigureMockMvc
public class TeamTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class GetTeams {



    }
}
