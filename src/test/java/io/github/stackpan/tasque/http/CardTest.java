package io.github.stackpan.tasque.http;

import io.github.stackpan.tasque.TestContainersConfig;
import io.github.stackpan.tasque.util.ExtMediaType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Import(TestContainersConfig.class)
@Sql(scripts = {
        "classpath:datasources/user.sql",
        "classpath:datasources/board.sql",
        "classpath:datasources/column.sql",
        "classpath:datasources/card.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(statements = {
        "delete from cards",
        "delete from columns",
        "delete from boards",
        "delete from users"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureMockMvc
public class CardTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String BOARD_ID = "0eec62bb-e1b6-40d8-aa3e-349853b96b6e";

    private final String COLUMN_ID = "7ab312f3-2661-4de4-9755-42d194c253c2";

    @Nested
    class GetCard {

        @Test
        void shouldReturnCard() throws Exception {
            var targetId = "d8355640-cf9c-45ec-a1ee-398157f5a544";

            mockMvc.perform(get("/api/boards/%s/columns/%s/cards/%s".formatted(BOARD_ID, COLUMN_ID, targetId))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                    )
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.id").value("d8355640-cf9c-45ec-a1ee-398157f5a544"),
                            jsonPath("$.body").value("Card 111"),
                            jsonPath("$.colorHex").value("#ffffff"),
                            jsonPath("$.createdAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$.updatedAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._links.boards.href").value(containsString("/api/boards")),
                            jsonPath("$._links.board.href").value(containsString("/api/boards/%s".formatted(BOARD_ID))),
                            jsonPath("$._links.columns.href").value(containsString("/api/boards/%s/columns".formatted(BOARD_ID))),
                            jsonPath("$._links.column.href").value(containsString("/api/boards/%s/columns/%s".formatted(BOARD_ID, COLUMN_ID))),
                            jsonPath("$._links.self.href").value(containsString("/api/boards/%s/columns/%s/cards/%s".formatted(BOARD_ID, COLUMN_ID, targetId)))
                    );
        }

        @Test
        void withInvalidUuidShouldReturnNotFound() throws Exception {
            mockMvc.perform(get("/api/boards/%s/columns/%s/cards/%s".formatted(BOARD_ID, COLUMN_ID, "invaliduuid"))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void withUnknownIdShouldReturnNotFound() throws Exception {
            mockMvc.perform(get("/api/boards/%s/columns/%s/cards/%s".formatted(BOARD_ID, COLUMN_ID, "97f21e9e-09bc-4aa2-a929-49387fcde4e1"))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void withUnownedBoardShouldReturnNotFound() throws Exception {
            mockMvc.perform(get("/api/boards/%s/columns/%s/cards/%s".formatted("7e885910-1df0-4744-8083-73e1d9769062", COLUMN_ID, "97f21e9e-09bc-4aa2-a929-49387fcde4e1"))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                    )
                    .andExpect(status().isNotFound());
        }
    }

}
