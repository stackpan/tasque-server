package io.github.stackpan.tasque.http;

import com.jayway.jsonpath.JsonPath;
import io.github.stackpan.tasque.TestContainersConfig;
import io.github.stackpan.tasque.util.ExtMediaType;
import io.github.stackpan.tasque.util.Regexps;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                            jsonPath("$._embedded.columns[*].position").value(
                                    containsInRelativeOrder(0, 1, 2)
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

        @Test
        void byUnknownBoardIdShouldNotFound() throws Exception {
            mockMvc.perform(get("/api/boards/%s/columns".formatted("0a2b0b12-1559-41cf-a4bd-a44fa0957b86"))
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
            mockMvc.perform(get("/api/boards/%s/columns".formatted("7e885910-1df0-4744-8083-73e1d9769062"))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                    )
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class PostColumn {

        @Test
        void firstRecordShouldCreatedAndReturnCreatedColumnAndStoredInDatabase() throws Exception {
            jdbcTemplate.update("DELETE FROM columns WHERE board_id = ?", UUID.fromString(BOARD_ID));

            var payload = """
                    {
                        "name": "Created Column",
                        "description": "Created Column description.",
                        "colorHex": "#ffffff"
                    }
                    """;

            mockMvc.perform(post("/api/boards/%s/columns".formatted(BOARD_ID))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.LOCATION, matchesPattern("^.*/api/boards/" + BOARD_ID + "/columns/" + Regexps.UUID)))
                    .andExpectAll(
                            jsonPath("$.id", matchesPattern(Regexps.UUID)),
                            jsonPath("$.position").value(0),
                            jsonPath("$.name").value(JsonPath.<String>read(payload, "$.name")),
                            jsonPath("$.description").value(JsonPath.<String>read(payload, "$.description")),
                            jsonPath("$.colorHex").value(JsonPath.<String>read(payload, "$.colorHex")),
                            jsonPath("$.createdAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$.updatedAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$._links.boards.href").value(containsString("/api/boards")),
                            jsonPath("$._links.board.href").value(containsString("/api/boards/%s".formatted(BOARD_ID))),
                            jsonPath("$._links.self.href", matchesPattern("^.*/api/boards/" + BOARD_ID + "/columns/" + Regexps.UUID))
//                            jsonPath("$._links.cards.href").value(containsString("/api/boards/%s/columns/%s/cards".formatted(BOARD_ID, targetId))),
                    )
                    .andDo(result -> {
                        var responseContent = result.getResponse().getContentAsString();

                        String id = JsonPath.read(responseContent, "$.id");
                        var count = jdbcTemplate.queryForObject("SELECT count(*) FROM columns WHERE id = ?", Integer.class, UUID.fromString(id));
                        assertEquals(1, count);
                    });
        }

        @Test
        void atFirstIndexShouldShiftAllOfOtherColumns() throws Exception {
            var payload = """
                    {
                        "name": "Created Column",
                        "description": "Created Column description.",
                        "colorHex": "#ffffff",
                        "position": 0
                    }
                    """;

            mockMvc.perform(post("/api/boards/%s/columns".formatted(BOARD_ID))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isCreated())
                    .andDo(result -> {
                        var responseContent = result.getResponse().getContentAsString();

                        String id = JsonPath.read(responseContent, "$.id");
                        String[] expectation = {
                                id,
                                "7ab312f3-2661-4de4-9755-42d194c253c2",
                                "89143482-fdbc-47fa-9a60-fca63335521f",
                                "f6968c9a-8fc3-4180-96be-a09809542339"
                        };
                        var boardColumnMaps = jdbcTemplate.queryForList("SELECT * FROM columns WHERE board_id = ? ORDER BY position", UUID.fromString(BOARD_ID))
                                .stream()
                                .map(objectMap -> objectMap.get("id").toString())
                                .toArray();
                        assertArrayEquals(expectation, boardColumnMaps);
                    });
        }

        @Test
        void atMiddleIndexShouldShiftOtherColumnsPartially() throws Exception {
            var payload = """
                    {
                        "name": "Created Column",
                        "description": "Created Column description.",
                        "colorHex": "#ffffff",
                        "position": 1
                    }
                    """;

            mockMvc.perform(post("/api/boards/%s/columns".formatted(BOARD_ID))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isCreated())
                    .andDo(result -> {
                        var responseContent = result.getResponse().getContentAsString();

                        String id = JsonPath.read(responseContent, "$.id");
                        String[] expectation = {
                                "7ab312f3-2661-4de4-9755-42d194c253c2",
                                id,
                                "89143482-fdbc-47fa-9a60-fca63335521f",
                                "f6968c9a-8fc3-4180-96be-a09809542339"
                        };
                        var boardColumnMaps = jdbcTemplate.queryForList("SELECT * FROM columns WHERE board_id = ? ORDER BY position", UUID.fromString(BOARD_ID))
                                .stream()
                                .map(objectMap -> objectMap.get("id").toString())
                                .toArray();
                        assertArrayEquals(expectation, boardColumnMaps);
                    });
        }

        @Test
        void noPositionKeyShouldBePlacedToLastIndex() throws Exception {
            var payload = """
                    {
                        "name": "Created Column",
                        "description": "Created Column description.",
                        "colorHex": "#ffffff"
                    }
                    """;

            mockMvc.perform(post("/api/boards/%s/columns".formatted(BOARD_ID))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isCreated())
                    .andDo(result -> {
                        var responseContent = result.getResponse().getContentAsString();

                        String id = JsonPath.read(responseContent, "$.id");
                        String[] expectation = {
                                "7ab312f3-2661-4de4-9755-42d194c253c2",
                                "89143482-fdbc-47fa-9a60-fca63335521f",
                                "f6968c9a-8fc3-4180-96be-a09809542339",
                                id
                        };
                        var boardColumnMaps = jdbcTemplate.queryForList("SELECT * FROM columns WHERE board_id = ? ORDER BY position", UUID.fromString(BOARD_ID))
                                .stream()
                                .map(objectMap -> objectMap.get("id").toString())
                                .toArray();
                        assertArrayEquals(expectation, boardColumnMaps);
                    });
        }

        @Test
        void withOverflowedPositionShouldBePreventedAndPlacedToLastIndex() throws Exception {
            var payload = """
                    {
                        "name": "Created Column",
                        "description": "Created Column description.",
                        "colorHex": "#ffffff",
                        "position": 100
                    }
                    """;

            mockMvc.perform(post("/api/boards/%s/columns".formatted(BOARD_ID))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isCreated())
                    .andDo(result -> {
                        var responseContent = result.getResponse().getContentAsString();

                        String id = JsonPath.read(responseContent, "$.id");
                        String[] expectation = {
                                "7ab312f3-2661-4de4-9755-42d194c253c2",
                                "89143482-fdbc-47fa-9a60-fca63335521f",
                                "f6968c9a-8fc3-4180-96be-a09809542339",
                                id
                        };
                        var boardColumnMaps = jdbcTemplate.queryForList("SELECT * FROM columns WHERE board_id = ? ORDER BY position", UUID.fromString(BOARD_ID))
                                .stream()
                                .map(objectMap -> objectMap.get("id").toString())
                                .toArray();
                        assertArrayEquals(expectation, boardColumnMaps);

                        Integer position = JsonPath.read(responseContent, "$.position");
                        assertEquals(3, position);
                    });
        }

        @Test
        void withInvalidPayloadShouldBadRequest() throws Exception {
            var payload = """
                    {
                        "name": 999,
                        "colorHex": "#fnffff",
                        "position": -1
                    }
                    """;

            mockMvc.perform(post("/api/boards/%s/columns".formatted(BOARD_ID))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.message").value("Invalid payload."),
                            jsonPath("$._embedded.payloadErrors.name").isArray(),
                            jsonPath("$._embedded.payloadErrors.description").doesNotExist(),
                            jsonPath("$._embedded.payloadErrors.colorHex").isArray(),
                            jsonPath("$._embedded.payloadErrors.position").isArray()
                    );
        }

        @Test
        void byUnknownBoardIdShouldNotFound() throws Exception {
            var payload = """
                    {
                        "name": "Created Column",
                        "description": "Created Column description.",
                        "colorHex": "#ffffff"
                    }
                    """;

            mockMvc.perform(get("/api/boards/%s/columns".formatted("0a2b0b12-1559-41cf-a4bd-a44fa0957b86"))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byUnownedBoardIdShouldNotFound() throws Exception {
            var payload = """
                    {
                        "name": "Created Column",
                        "description": "Created Column description.",
                        "colorHex": "#ffffff"
                    }
                    """;

            mockMvc.perform(get("/api/boards/%s/columns".formatted("7e885910-1df0-4744-8083-73e1d9769062"))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isNotFound());
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
                            jsonPath("$.position").value(0),
                            jsonPath("$.name").value("Column 11"),
                            jsonPath("$.description").value("A long description of Column 11"),
                            jsonPath("$.colorHex").value("#ffffff"),
                            jsonPath("$.createdAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$.updatedAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._links.boards.href").value(containsString("/api/boards")),
                            jsonPath("$._links.board.href").value(containsString("/api/boards/%s".formatted(BOARD_ID))),
                            jsonPath("$._links.self.href").value(containsString("/api/boards/%s/columns/%s".formatted(BOARD_ID, targetId))),
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

    @Nested
    class UpdateColumn {

        private final String COLUMN_ID = "89143482-fdbc-47fa-9a60-fca63335521f";

        @Test
        void shouldReturnUpdatedColumnAndChangedOnDatabase() throws Exception {
            var oldColumnMap = jdbcTemplate.queryForMap("SELECT * FROM columns WHERE columns.id = ?", UUID.fromString(COLUMN_ID));

            var payload = """
                    {
                        "name": "Updated Column 12",
                        "position": 1,
                        "description": "A long description of Updated Column 12",
                        "colorHex": "#000000"
                    }
                    """;

            mockMvc.perform(put("/api/boards/%s/columns/%s".formatted(BOARD_ID, COLUMN_ID))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.id").value(COLUMN_ID),
                            jsonPath("$.position").value(JsonPath.<Integer>read(payload, "$.position")),
                            jsonPath("$.name").value(JsonPath.<String>read(payload, "$.name")),
                            jsonPath("$.description").value(JsonPath.<String>read(payload, "$.description")),
                            jsonPath("$.colorHex").value(JsonPath.<String>read(payload, "$.colorHex")),
                            jsonPath("$.createdAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$.updatedAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$._links.boards.href").value(containsString("/api/boards")),
                            jsonPath("$._links.board.href").value(containsString("/api/boards/%s".formatted(BOARD_ID))),
                            jsonPath("$._links.self.href", matchesPattern("^.*/api/boards/" + BOARD_ID + "/columns/" + Regexps.UUID))
//                            jsonPath("$._links.cards.href").value(containsString("/api/boards/%s/columns/%s/cards".formatted(BOARD_ID, targetId))),
                    );

            var updatedColumnMap = jdbcTemplate.queryForMap("SELECT * FROM columns WHERE columns.id = ?", UUID.fromString(COLUMN_ID));

            long expectationPosition = ((Integer) JsonPath.read(payload, "$.position")).longValue();
            long actualPosition = (Long) updatedColumnMap.get("position");
            assertEquals(expectationPosition, actualPosition);

            assertEquals(JsonPath.<String>read(payload, "$.name"), updatedColumnMap.get("name"));

            assertEquals(JsonPath.<String>read(payload, "$.description"), updatedColumnMap.get("description"));

            assertEquals(JsonPath.<String>read(payload, "$.colorHex"), updatedColumnMap.get("color_hex"));

            var oldBoardUpdatedAt = ((Timestamp) oldColumnMap.get("updated_at")).toInstant();
            var updatedBoardUpdatedAt = ((Timestamp) updatedColumnMap.get("updated_at")).toInstant();
            assertTrue(updatedBoardUpdatedAt.isAfter(oldBoardUpdatedAt));
        }

        @Test
        void movePositionToLeft() throws Exception {
            var payload = """
                    {
                        "name": "Column 12",
                        "position": 0,
                        "description": "A long description of Column 12",
                        "colorHex": "#ffffff"
                    }
                    """;

            mockMvc.perform(put("/api/boards/%s/columns/%s".formatted(BOARD_ID, COLUMN_ID))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isOk());

            String[] expectation = {
                    COLUMN_ID,
                    "7ab312f3-2661-4de4-9755-42d194c253c2",
                    "f6968c9a-8fc3-4180-96be-a09809542339"
            };
            var boardColumnMaps = jdbcTemplate.queryForList("SELECT * FROM columns WHERE board_id = ? ORDER BY position", UUID.fromString(BOARD_ID))
                    .stream()
                    .map(objectMap -> objectMap.get("id").toString())
                    .toArray();
            assertArrayEquals(expectation, boardColumnMaps);
        }

        @Test
        void movePositionToRight() throws Exception {
            var payload = """
                    {
                        "name": "Column 12",
                        "position": 2,
                        "description": "A long description of Column 12",
                        "colorHex": "#ffffff"
                    }
                    """;

            mockMvc.perform(put("/api/boards/%s/columns/%s".formatted(BOARD_ID, COLUMN_ID))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isOk());

            String[] expectation = {
                    "7ab312f3-2661-4de4-9755-42d194c253c2",
                    "f6968c9a-8fc3-4180-96be-a09809542339",
                    COLUMN_ID
            };
            var boardColumnMaps = jdbcTemplate.queryForList("SELECT * FROM columns WHERE board_id = ? ORDER BY position", UUID.fromString(BOARD_ID))
                    .stream()
                    .map(objectMap -> objectMap.get("id").toString())
                    .toArray();
            assertArrayEquals(expectation, boardColumnMaps);
        }

        @Test
        void withInvalidPayloadShouldBadRequest() throws Exception {
            var payload = """
                    {
                        "name": 999,
                        "colorHex": "#fnffff"
                    }
                    """;

            mockMvc.perform(put("/api/boards/%s/columns/%s".formatted(BOARD_ID, COLUMN_ID))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.message").value("Invalid payload."),
                            jsonPath("$._embedded.payloadErrors.name").isArray(),
                            jsonPath("$._embedded.payloadErrors.description").doesNotExist(),
                            jsonPath("$._embedded.payloadErrors.colorHex").isArray(),
                            jsonPath("$._embedded.payloadErrors.position").isArray()
                    );
        }

        @Test
        void byUnknownIdShouldNotFound() throws Exception {
            var payload = """
                    {
                        "name": "Updated Column 12",
                        "position": 1,
                        "description": "A long description of Updated Column 12",
                        "colorHex": "#000000"
                    }
                    """;

            mockMvc.perform(put("/api/boards/%s/columns/%s".formatted(BOARD_ID, "855a4d8e-ae59-4850-8a4e-f7579e2517e7"))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byInvalidUuidShouldNotFound() throws Exception {
            var payload = """
                    {
                        "name": "Updated Column 12",
                        "position": 1,
                        "description": "A long description of Updated Column 12",
                        "colorHex": "#000000"
                    }
                    """;

            mockMvc.perform(put("/api/boards/%s/columns/%s".formatted(BOARD_ID, "invaliduuid"))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byUnownedBoardIdShouldNotFound() throws Exception {
            var payload = """
                    {
                        "name": "Updated Column 12",
                        "position": 1,
                        "description": "A long description of Updated Column 12",
                        "colorHex": "#000000"
                    }
                    """;

            mockMvc.perform(put("/api/boards/%s/columns/%s".formatted("7e885910-1df0-4744-8083-73e1d9769062", "e6be59bb-4178-4869-bfb2-1d09bc5af558"))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class DeleteColumn {

        @Test
        void shouldNoContentAndMissingFromDatabase() throws Exception {
            String columnId = "89143482-fdbc-47fa-9a60-fca63335521f";

            mockMvc.perform(delete("/api/boards/%s/columns/%s".formatted(BOARD_ID, columnId))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                    )
                    .andExpect(status().isNoContent());

            var count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM columns WHERE id = ?", Integer.class, UUID.fromString(columnId));
            assertEquals(count, 0);

            var highestColumnPosition = jdbcTemplate.queryForObject("SELECT MAX(position) FROM columns WHERE columns.board_id = ?", Long.class, UUID.fromString(BOARD_ID));
            assertEquals(1, highestColumnPosition);
        }

        @Test
        void byUnknownIdShouldNotFound() throws Exception {
            mockMvc.perform(delete("/api/boards/%s/columns/%s".formatted(BOARD_ID, "855a4d8e-ae59-4850-8a4e-f7579e2517e7"))
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
            mockMvc.perform(delete("/api/boards/%s/columns/%s".formatted(BOARD_ID, "invaliduuid"))
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
            mockMvc.perform(delete("/api/boards/%s/columns/%s".formatted("7e885910-1df0-4744-8083-73e1d9769062", "e6be59bb-4178-4869-bfb2-1d09bc5af558"))
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
