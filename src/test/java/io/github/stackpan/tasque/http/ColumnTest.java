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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Import(TestContainersConfig.class)
@Sql(scripts = {
        "classpath:datasources/user.sql",
        "classpath:datasources/board.sql",
        "classpath:datasources/column.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(statements = {"delete from columns", "delete from boards", "delete from users"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureMockMvc
public class ColumnTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String BOARD_ID = "0eec62bb-e1b6-40d8-aa3e-349853b96b6e";

    @Nested
    class ListColumnInABoard {

        @Test
        void shouldReturnListOfColumn() throws Exception {
            mockMvc.perform(get("/api/boards/%s/columns".formatted(BOARD_ID))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                    )
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$._embedded.columns.length()").value(3),
                            jsonPath("$._embedded.columns[*].id").value(
                                    containsInAnyOrder("7ab312f3-2661-4de4-9755-42d194c253c2", "89143482-fdbc-47fa-9a60-fca63335521f", "f6968c9a-8fc3-4180-96be-a09809542339")
                            ),
                            jsonPath("$._embedded.columns[*].name").value(
                                    containsInAnyOrder("Column 11", "Column 12", "Column 13")
                            ),
                            jsonPath("$._embedded.columns[*].description").value(
                                    containsInAnyOrder("A long description of Column 11", "A long description of Column 12", "A long description of Column 13")
                            ),
                            jsonPath("$._embedded.columns[*].colorHex").value(
                                    containsInAnyOrder("#ffffff", "#ffffff", "#ffffff")
                            ),
                            jsonPath("$._embedded.columns[*].nextColumnId").value(
                                    containsInRelativeOrder("89143482-fdbc-47fa-9a60-fca63335521f", "f6968c9a-8fc3-4180-96be-a09809542339", (String) null)
                            ),
                            jsonPath("$._embedded.columns[*].createdAt").value(
                                    containsInAnyOrder("2024-07-28T00:00:00Z", "2024-07-28T00:00:01Z", "2024-07-28T00:00:02Z")
                            ),
                            jsonPath("$._embedded.columns[*].updatedAt").value(
                                    containsInAnyOrder("2024-07-28T00:00:00Z", "2024-07-28T00:00:01Z", "2024-07-28T00:00:02Z")
                            ),
                            jsonPath("$._embedded.columns[*]._links.boards.href", everyItem(containsString("/api/boards"))),
                            jsonPath("$._embedded.columns[*]._links.board.href", everyItem(containsString("/api/boards/%s".formatted(BOARD_ID)))),
                            jsonPath("$._embedded.columns[*]._links.columns.href", everyItem(containsString("/api/boards/%s/columns".formatted(BOARD_ID)))),
                            jsonPath("$._embedded.columns[*]._links.self.href").value(
                                    containsInAnyOrder(
                                            containsString("/api/boards/%s/columns/%s".formatted(BOARD_ID, "7ab312f3-2661-4de4-9755-42d194c253c2")),
                                            containsString("/api/boards/%s/columns/%s".formatted(BOARD_ID, "89143482-fdbc-47fa-9a60-fca63335521f")),
                                            containsString("/api/boards/%s/columns/%s".formatted(BOARD_ID, "f6968c9a-8fc3-4180-96be-a09809542339"))
                                    )
                            ),
                            jsonPath("$._embedded.columns[*]._links.next.href").value(
                                    containsInRelativeOrder(
                                            containsString("/api/boards/%s/columns/%s".formatted(BOARD_ID, "89143482-fdbc-47fa-9a60-fca63335521f")),
                                            containsString("/api/boards/%s/columns/%s".formatted(BOARD_ID, "f6968c9a-8fc3-4180-96be-a09809542339"))
                                    )
                            ),
//                            jsonPath("$._embedded.columns[*]._links.cards.href").value(
//                                    containsInAnyOrder(
//                                            "/api/boards/%s/columns/%s/cards".formatted(BOARD_ID, "7ab312f3-2661-4de4-9755-42d194c253c2"),
//                                            "/api/boards/%s/columns/%s/cards".formatted(BOARD_ID, "89143482-fdbc-47fa-9a60-fca63335521f"),
//                                            "/api/boards/%s/columns/%s/cards".formatted(BOARD_ID, "f6968c9a-8fc3-4180-96be-a09809542339")
//                                    )
//                            ),
                            jsonPath("$._embedded.columns[*]._embedded.cards").isArray(),
                            jsonPath("$._links.boards.href").value(containsString("/api/boards")),
                            jsonPath("$._links.board.href").value(containsString("/api/boards/%s".formatted(BOARD_ID))),
                            jsonPath("$._links.self.href").value(containsString("/api/boards/%s/columns".formatted(BOARD_ID)))
                    );
        }
    }

    @Nested
    class GetColumn {

        @Test
        void shouldReturnColumn() throws Exception {
            var targetId = "7ab312f3-2661-4de4-9755-42d194c253c2";

            mockMvc.perform(get("/api/boards/%s/columns/%s".formatted(BOARD_ID, targetId))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                    )
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.id").value(targetId),
                            jsonPath("$.name").value("Column 11"),
                            jsonPath("$.description").value("A long description of Column 11"),
                            jsonPath("$.colorHex").value("#ffffff"),
                            jsonPath("$.nextColumnId").value("89143482-fdbc-47fa-9a60-fca63335521f"),
                            jsonPath("$.createdAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$.updatedAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._links.boards.href").value(containsString("/api/boards")),
                            jsonPath("$._links.board.href").value(containsString("/api/boards/%s".formatted(BOARD_ID))),
                            jsonPath("$._links.self.href").value(containsString("/api/boards/%s/columns/%s".formatted(BOARD_ID, targetId))),
                            jsonPath("$._links.next.href").value(containsString("/api/boards/%s/columns/%s".formatted(BOARD_ID, "89143482-fdbc-47fa-9a60-fca63335521f"))),
//                            jsonPath("$._links.cards.href").value(containsString("/api/boards/%s/columns/%s/cards".formatted(BOARD_ID, targetId))),
                            jsonPath("$._embedded.cards").isArray()
                    );
        }

        @Test
        void byUnknownIdShouldNotFound() throws Exception {
            mockMvc.perform(get("/api/boards/%s/columns/%s".formatted(BOARD_ID, "855a4d8e-ae59-4850-8a4e-f7579e2517e7"))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byInvalidUuidShouldNotFound() throws Exception {
            mockMvc.perform(get("/api/boards/%s/columns/%s".formatted(BOARD_ID, "invaliduuid"))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byUnownedBoardIdShouldNotFound() throws Exception {
            mockMvc.perform(get("/api/boards/%s/columns/%s".formatted("7e885910-1df0-4744-8083-73e1d9769062", "e6be59bb-4178-4869-bfb2-1d09bc5af558"))
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
