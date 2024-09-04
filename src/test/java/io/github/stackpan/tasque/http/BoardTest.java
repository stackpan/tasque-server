package io.github.stackpan.tasque.http;

import com.jayway.jsonpath.JsonPath;
import io.github.stackpan.tasque.TestContainersConfig;
import io.github.stackpan.tasque.UserMocks;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Import(TestContainersConfig.class)
@Sql(
        scripts = {"classpath:datasources/user.sql", "classpath:datasources/board.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        statements = {"DELETE FROM boards", "DELETE FROM users"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
@AutoConfigureMockMvc
public class BoardTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Nested
    class ListBoard {

        @Test
        void shouldReturnListOfBoard() throws Exception {
            mockMvc.perform(get("/api/boards")
                            .with(UserMocks.rizkyJwt())
                    )
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$._embedded.boards.length()").value(3),
                            jsonPath("$._embedded.boards[*].id").value(
                                    containsInAnyOrder("0eec62bb-e1b6-40d8-aa3e-349853b96b6e", "1baeb76d-0cdc-4eb4-885f-890b8cbab09e", "037b473d-cdd1-42e6-b1f1-4c6a3e1307f9")
                            ),
                            jsonPath("$._embedded.boards[*].name").value(
                                    containsInAnyOrder("Board One", "Board Two", "Board Three")
                            ),
                            jsonPath("$._embedded.boards[*].description").value(
                                    containsInAnyOrder("A long description of Board One.", "A long description of Board Two.", "A long description of Board Three.")
                            ),
                            jsonPath("$._embedded.boards[*].bannerPictureUrl").value(
                                    containsInAnyOrder((String) null, (String) null, (String) null)
                            ),
                            jsonPath("$._embedded.boards[*].colorHex").value(
                                    containsInAnyOrder("#000000", "#000000", "#000000")
                            ),
                            jsonPath("$._embedded.boards[*].ownerId", everyItem(equalTo("172e7077-76a4-4fa3-879d-6ec767c655e6"))),
                            jsonPath("$._embedded.boards[*].ownerType", everyItem(equalTo("USER"))),
                            jsonPath("$._embedded.boards[*].createdAt").value(
                                    containsInAnyOrder("2024-07-28T00:00:00Z", "2024-07-28T00:00:01Z", "2024-07-28T00:00:02Z")
                            ),
                            jsonPath("$._embedded.boards[*].updatedAt").value(
                                    containsInAnyOrder("2024-07-28T00:00:00Z", "2024-07-28T00:00:01Z", "2024-07-28T00:00:02Z")
                            ),
                            jsonPath("$._embedded.boards[*]._embedded.owner.id", everyItem(equalTo("172e7077-76a4-4fa3-879d-6ec767c655e6"))),
                            jsonPath("$._embedded.boards[*]._embedded.owner.username", everyItem(equalTo("rizky"))),
                            jsonPath("$._embedded.boards[*]._embedded.owner.email", everyItem(equalTo("rizky@example.com"))),
                            jsonPath("$._embedded.boards[*]._embedded.owner.firstName", everyItem(equalTo("Rizky"))),
                            jsonPath("$._embedded.boards[*]._embedded.owner.lastName", everyItem(equalTo("Anto"))),
                            jsonPath("$._embedded.boards[*]._embedded.owner.profilePictureUrl", everyItem(nullValue())),
                            jsonPath("$._embedded.boards[*]._embedded.owner.emailVerifiedAt", everyItem(nullValue())),
                            jsonPath("$._embedded.boards[*]._embedded.owner.createdAt", everyItem(equalTo("2024-07-28T00:00:00Z"))),
                            jsonPath("$._embedded.boards[*]._embedded.owner.updatedAt", everyItem(equalTo("2024-07-28T00:00:00Z"))),
                            jsonPath("$._embedded.boards[*]._embedded.owner._links.self.href", everyItem(containsString("/users/172e7077-76a4-4fa3-879d-6ec767c655e6"))),
                            jsonPath("$._links.self.href").value(containsString("/api/boards"))
                    );
        }
    }

    @Nested
    class CreateBoard {

        @Test
        void shouldCreatedAndReturnCreatedBoardAndStoredInDatabase() throws Exception {
            var payload = """
                    {
                        "name": "Newly Created Board",
                        "description": "A long description of Newly Created Board.",
                        "colorHex": "#ffffff"
                    }
                    """;

            mockMvc.perform(post("/api/boards")
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpect(header().string(HttpHeaders.LOCATION, matchesPattern("^.*/api/boards/" + Regexps.UUID)))
                    .andExpectAll(
                            jsonPath("$.id", matchesPattern(Regexps.UUID)),
                            jsonPath("$.name").value("Newly Created Board"),
                            jsonPath("$.description").value("A long description of Newly Created Board."),
                            jsonPath("$.bannerPictureUrl").isEmpty(),
                            jsonPath("$.colorHex").value("#ffffff"),
                            jsonPath("$.ownerId").value("172e7077-76a4-4fa3-879d-6ec767c655e6"),
                            jsonPath("$.ownerType").value("USER"),
                            jsonPath("$.createdAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$.updatedAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$._embedded.owner.id").value("172e7077-76a4-4fa3-879d-6ec767c655e6"),
                            jsonPath("$._embedded.owner.username").value("rizky"),
                            jsonPath("$._embedded.owner.email").value("rizky@example.com"),
                            jsonPath("$._embedded.owner.firstName").value("Rizky"),
                            jsonPath("$._embedded.owner.lastName").value("Anto"),
                            jsonPath("$._embedded.owner.profilePictureUrl").isEmpty(),
                            jsonPath("$._embedded.owner.emailVerifiedAt").isEmpty(),
                            jsonPath("$._embedded.owner.createdAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._embedded.owner.updatedAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._embedded.owner._links.self.href").value(containsString("/users/172e7077-76a4-4fa3-879d-6ec767c655e6")),
                            jsonPath("$._links.boards.href").value(containsString("/api/boards")),
                            jsonPath("$._links.self.href", matchesPattern("^.*/api/boards/" + Regexps.UUID))
                    )
                    .andDo(result -> {
                        var responseContent = result.getResponse().getContentAsString();

                        var createdId = JsonPath.<String>read(responseContent, "$.id");
                        var count = jdbcTemplate.queryForObject("select count(*) from boards where id = ?", Integer.class, UUID.fromString(createdId));
                        assertEquals(count, 1);
                    });
        }

        @Test
        void withNullablePayloadShouldCreatedAndStoredInDatabase() throws Exception {
            var payload = """
                    {
                        "name": "Newly Created Board",
                        "description": null,
                        "colorHex": null
                    }
                    """;

            mockMvc.perform(post("/api/boards")
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpect(header().string(HttpHeaders.LOCATION, matchesPattern("^.*/api/boards/" + Regexps.UUID)))
                    .andExpectAll(
                            jsonPath("$.id", matchesPattern(Regexps.UUID)),
                            jsonPath("$.name").value("Newly Created Board"),
                            jsonPath("$.description").isEmpty(),
                            jsonPath("$.bannerPictureUrl").isEmpty(),
                            jsonPath("$.colorHex").isEmpty(),
                            jsonPath("$.ownerId").value("172e7077-76a4-4fa3-879d-6ec767c655e6"),
                            jsonPath("$.ownerType").value("USER"),
                            jsonPath("$.createdAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$.updatedAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$._embedded.owner.id").value("172e7077-76a4-4fa3-879d-6ec767c655e6"),
                            jsonPath("$._embedded.owner.username").value("rizky"),
                            jsonPath("$._embedded.owner.email").value("rizky@example.com"),
                            jsonPath("$._embedded.owner.firstName").value("Rizky"),
                            jsonPath("$._embedded.owner.lastName").value("Anto"),
                            jsonPath("$._embedded.owner.profilePictureUrl").isEmpty(),
                            jsonPath("$._embedded.owner.emailVerifiedAt").isEmpty(),
                            jsonPath("$._embedded.owner.createdAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._embedded.owner.updatedAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._embedded.owner._links.self.href").value(containsString("/users/172e7077-76a4-4fa3-879d-6ec767c655e6")),
                            jsonPath("$._links.boards.href").value(containsString("/api/boards")),
                            jsonPath("$._links.self.href", matchesPattern("^.*/api/boards/" + Regexps.UUID))
                    )
                    .andDo(result -> {
                        var responseContent = result.getResponse().getContentAsString();

                        var createdId = JsonPath.<String>read(responseContent, "$.id");
                        var count = jdbcTemplate.queryForObject("select count(*) from boards where id = ?", Integer.class, UUID.fromString(createdId));
                        assertEquals(count, 1);
                    });
        }


        @Test
        void withInvalidPayloadShouldBadRequest() throws Exception {
            var payload = """
                    {
                        "name": 999,
                        "colorHex": "#fnffff"
                    }
                    """;

            mockMvc.perform(post("/api/boards")
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.message").value("Invalid payload."),
                            jsonPath("$._embedded.payloadErrors.name").isArray(),
                            jsonPath("$._embedded.payloadErrors.description").doesNotExist(),
                            jsonPath("$._embedded.payloadErrors.colorHex").isArray()
                    );
        }
    }

    @Nested
    class GetBoard {

        @Test
        void shouldReturnBoard() throws Exception {
            var targetId = "0eec62bb-e1b6-40d8-aa3e-349853b96b6e";

            mockMvc.perform(get("/api/boards/%s".formatted(targetId))
                            .with(UserMocks.rizkyJwt())
                    )
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.id").value(targetId),
                            jsonPath("$.name").value("Board One"),
                            jsonPath("$.description").value("A long description of Board One."),
                            jsonPath("$.bannerPictureUrl").isEmpty(),
                            jsonPath("$.colorHex").value("#000000"),
                            jsonPath("$.ownerId").value("172e7077-76a4-4fa3-879d-6ec767c655e6"),
                            jsonPath("$.ownerType").value("USER"),
                            jsonPath("$.createdAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$.updatedAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._embedded.owner.id").value("172e7077-76a4-4fa3-879d-6ec767c655e6"),
                            jsonPath("$._embedded.owner.username").value("rizky"),
                            jsonPath("$._embedded.owner.email").value("rizky@example.com"),
                            jsonPath("$._embedded.owner.firstName").value("Rizky"),
                            jsonPath("$._embedded.owner.lastName").value("Anto"),
                            jsonPath("$._embedded.owner.profilePictureUrl").isEmpty(),
                            jsonPath("$._embedded.owner.emailVerifiedAt").isEmpty(),
                            jsonPath("$._embedded.owner.createdAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._embedded.owner.updatedAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._embedded.owner._links.self.href").value(containsString("/users/172e7077-76a4-4fa3-879d-6ec767c655e6")),
                            jsonPath("$._links.boards.href").value(containsString("/api/boards")),
                            jsonPath("$._links.self.href").value(containsString("/api/boards/%s".formatted(targetId)))
                    );
        }

        @Test
        void byUnknownIdShouldNotFound() throws Exception {
            mockMvc.perform(get("/api/boards/75d46c19-d28e-4a8d-8e7c-19220b15c507")
                            .with(UserMocks.rizkyJwt())
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byInvalidUuidShouldNotFound() throws Exception {
            mockMvc.perform(get("/api/boards/invaliduuid")
                            .with(UserMocks.rizkyJwt())
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byUnownedBoardIdShouldNotFound() throws Exception {
            mockMvc.perform(get("/api/boards/7e885910-1df0-4744-8083-73e1d9769062")
                            .with(UserMocks.rizkyJwt())
                    )
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class UpdateBoard {

        private final String TARGET_ID = "0eec62bb-e1b6-40d8-aa3e-349853b96b6e";

        @Test
        void shouldReturnUpdatedBoardAndChangedOnDatabase() throws Exception {
            var oldBoardMap = jdbcTemplate.queryForMap("select * from boards where id = ?", UUID.fromString(TARGET_ID));

            var payload = """
                    {
                        "name": "Updated Board One",
                        "description": "A long description of Updated Board One.",
                        "colorHex": "#ffffff"
                    }
                    """;

            mockMvc.perform(put("/api/boards/%s".formatted(TARGET_ID))
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.id").value(TARGET_ID),
                            jsonPath("$.name").value(JsonPath.<String>read(payload, "$.name")),
                            jsonPath("$.description").value(JsonPath.<String>read(payload, "$.description")),
                            jsonPath("$.bannerPictureUrl").isEmpty(),
                            jsonPath("$.colorHex").value(JsonPath.<String>read(payload, "$.colorHex")),
                            jsonPath("$.ownerId").value("172e7077-76a4-4fa3-879d-6ec767c655e6"),
                            jsonPath("$.ownerType").value("USER"),
                            jsonPath("$.createdAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$.updatedAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$._embedded.owner.id").value("172e7077-76a4-4fa3-879d-6ec767c655e6"),
                            jsonPath("$._embedded.owner.username").value("rizky"),
                            jsonPath("$._embedded.owner.email").value("rizky@example.com"),
                            jsonPath("$._embedded.owner.firstName").value("Rizky"),
                            jsonPath("$._embedded.owner.lastName").value("Anto"),
                            jsonPath("$._embedded.owner.profilePictureUrl").isEmpty(),
                            jsonPath("$._embedded.owner.emailVerifiedAt").isEmpty(),
                            jsonPath("$._embedded.owner.createdAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._embedded.owner.updatedAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._embedded.owner._links.self.href").value(containsString("/users/172e7077-76a4-4fa3-879d-6ec767c655e6")),
                            jsonPath("$._links.boards.href").value(containsString("/api/boards")),
                            jsonPath("$._links.self.href").value(containsString("/api/boards/%s".formatted(TARGET_ID)))
                    )
                    .andDo(result -> {
                        var updatedBoardMap = jdbcTemplate.queryForMap("select * from boards where id = ?", UUID.fromString(TARGET_ID));

                        assertEquals(updatedBoardMap.get("name"), JsonPath.<String>read(payload, "$.name"));
                        assertEquals(updatedBoardMap.get("description"), JsonPath.<String>read(payload, "$.description"));
                        assertEquals(updatedBoardMap.get("color_hex"), JsonPath.<String>read(payload, "$.colorHex"));

                        var oldBoardUpdatedAt = ((Timestamp) oldBoardMap.get("updated_at")).toInstant();
                        var updatedBoardUpdatedAt = ((Timestamp) updatedBoardMap.get("updated_at")).toInstant();
                        assertTrue(updatedBoardUpdatedAt.isAfter(oldBoardUpdatedAt));
                    });
        }

        @Test
        void withNullablePayloadShouldReturnUpdatedBoardAndChangedOnDatabase() throws Exception {
            var oldBoardMap = jdbcTemplate.queryForMap("select * from boards where id = ?", UUID.fromString(TARGET_ID));

            var payload = """
                    {
                        "name": "Newly Created Board",
                        "description": null,
                        "colorHex": null
                    }
                    """;

            mockMvc.perform(put("/api/boards/%s".formatted(TARGET_ID))
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.id").value(TARGET_ID),
                            jsonPath("$.name").value(JsonPath.<String>read(payload, "$.name")),
                            jsonPath("$.description").value(JsonPath.<String>read(payload, "$.description")),
                            jsonPath("$.bannerPictureUrl").isEmpty(),
                            jsonPath("$.colorHex").value(JsonPath.<String>read(payload, "$.colorHex")),
                            jsonPath("$.ownerId").value("172e7077-76a4-4fa3-879d-6ec767c655e6"),
                            jsonPath("$.ownerType").value("USER"),
                            jsonPath("$.createdAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$.updatedAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$._embedded.owner.id").value("172e7077-76a4-4fa3-879d-6ec767c655e6"),
                            jsonPath("$._embedded.owner.username").value("rizky"),
                            jsonPath("$._embedded.owner.email").value("rizky@example.com"),
                            jsonPath("$._embedded.owner.firstName").value("Rizky"),
                            jsonPath("$._embedded.owner.lastName").value("Anto"),
                            jsonPath("$._embedded.owner.profilePictureUrl").isEmpty(),
                            jsonPath("$._embedded.owner.emailVerifiedAt").isEmpty(),
                            jsonPath("$._embedded.owner.createdAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._embedded.owner.updatedAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._embedded.owner._links.self.href").value(containsString("/users/172e7077-76a4-4fa3-879d-6ec767c655e6")),
                            jsonPath("$._links.boards.href").value(containsString("/api/boards")),
                            jsonPath("$._links.self.href").value(containsString("/api/boards/%s".formatted(TARGET_ID)))
                    )
                    .andDo(result -> {
                        var updatedBoardMap = jdbcTemplate.queryForMap("select * from boards where id = ?", UUID.fromString(TARGET_ID));

                        assertEquals(updatedBoardMap.get("name"), JsonPath.<String>read(payload, "$.name"));
                        assertNull(updatedBoardMap.get("description"));
                        assertNull(updatedBoardMap.get("color_hex"));

                        var oldBoardUpdatedAt = ((Timestamp) oldBoardMap.get("updated_at")).toInstant();
                        var updatedBoardUpdatedAt = ((Timestamp) updatedBoardMap.get("updated_at")).toInstant();
                        assertTrue(updatedBoardUpdatedAt.isAfter(oldBoardUpdatedAt));
                    });
        }

        @Test
        void withInvalidPayloadShouldBadRequest() throws Exception {
            var payload = """
                    {
                        "name": 999,
                        "colorHex": "#fnffff"
                    }
                    """;

            mockMvc.perform(put("/api/boards/%s".formatted(TARGET_ID))
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.message").value("Invalid payload."),
                            jsonPath("$._embedded.payloadErrors.name").isArray(),
                            jsonPath("$._embedded.payloadErrors.description").doesNotExist(),
                            jsonPath("$._embedded.payloadErrors.colorHex").isArray()
                    );
        }

        @Test
        void byUnknownIdShouldNotFound() throws Exception {
            var payload = """
                    {
                        "name": "Updated Board One",
                        "description": "A long description of Updated Board One.",
                        "colorHex": "#ffffff"
                    }
                    """;

            mockMvc.perform(put("/api/boards/75d46c19-d28e-4a8d-8e7c-19220b15c507")
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byInvalidUuidShouldNotFound() throws Exception {
            var payload = """
                    {
                        "name": "Updated Board One",
                        "description": "A long description of Updated Board One.",
                        "colorHex": "#ffffff"
                    }
                    """;

            mockMvc.perform(put("/api/boards/invaliduuid")
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byUnownedBoardIdShouldNotFound() throws Exception {
            var payload = """
                    {
                        "name": "Updated Board One",
                        "description": "A long description of Updated Board One.",
                        "colorHex": "#ffffff"
                    }
                    """;

            mockMvc.perform(get("/api/boards/7e885910-1df0-4744-8083-73e1d9769062")
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class DeleteBoard {

        @Test
        void shouldNoContentAndMissingFromDatabase() throws Exception {
            String TARGET_ID = "0eec62bb-e1b6-40d8-aa3e-349853b96b6e";

            mockMvc.perform(delete("/api/boards/%s".formatted(TARGET_ID))
                            .with(UserMocks.rizkyJwt())
                    )
                    .andExpect(status().isNoContent());

            var count = jdbcTemplate.queryForObject("select count(*) from boards where id = ?", Integer.class, UUID.fromString(TARGET_ID));
            assertEquals(count, 0);
        }

        @Test
        void byUnknownIdShouldNotFound() throws Exception {
            mockMvc.perform(delete("/api/boards/75d46c19-d28e-4a8d-8e7c-19220b15c507")
                            .with(UserMocks.rizkyJwt())
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byInvalidUuidShouldNotFound() throws Exception {
            mockMvc.perform(delete("/api/boards/invaliduuid")
                            .with(UserMocks.rizkyJwt())
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byUnownedBoardIdShouldNotFound() throws Exception {
            mockMvc.perform(delete("/api/boards/7e885910-1df0-4744-8083-73e1d9769062")
                            .with(UserMocks.rizkyJwt())
                    )
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @Sql(
            scripts = {
                    "classpath:datasources/user.sql",
                    "classpath:datasources/board.sql",
                    "classpath:datasources/column.sql",
                    "classpath:datasources/card.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            statements = {
                    "DELETE FROM cards",
                    "DELETE FROM columns",
                    "DELETE FROM boards",
                    "DELETE FROM users"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    class MoveCard {

        private final String BOARD_ID = "0eec62bb-e1b6-40d8-aa3e-349853b96b6e";

        @Test
        void shouldReturnListOfColumnsWithCardLoadedAndMovedInDatabase() throws Exception {
            final String TARGET_CARD_ID = "d8355640-cf9c-45ec-a1ee-398157f5a544";
            final String DESTINATION_COLUMN_ID = "89143482-fdbc-47fa-9a60-fca63335521f";

            var payload = """
                    {
                        "targetCardId": "%s",
                        "destinationColumnId": "%s"
                    }
                    """.formatted(TARGET_CARD_ID, DESTINATION_COLUMN_ID);

            mockMvc.perform(post("/api/boards/%s/move-card".formatted(BOARD_ID))
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$._embedded.columns.length()").value(3),
                            jsonPath("$._embedded.columns[*]._embedded.cards.length()").value(
                                    containsInAnyOrder(1, 1, 0)
                            ),
                            jsonPath("$._embedded.columns[?(@.id == '%s')]._embedded.cards.length()".formatted(DESTINATION_COLUMN_ID)).value(1)
                    );

            var column1CardsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cards WHERE column_id = ?", Integer.class, UUID.fromString("7ab312f3-2661-4de4-9755-42d194c253c2"));
            assertEquals(1, column1CardsCount);

            var column2CardsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cards WHERE id = ? AND column_id = ?", Integer.class, UUID.fromString(TARGET_CARD_ID), UUID.fromString(DESTINATION_COLUMN_ID));
            assertEquals(1, column2CardsCount);
        }

        @Test
        void invalidPayloadShouldBadRequest() throws Exception {
            var payload = """
                    {
                        "targetCardId": 900
                    }
                    """;

            mockMvc.perform(post("/api/boards/%s/move-card".formatted(BOARD_ID))
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.message").value("Invalid payload."),
                            jsonPath("$._embedded.payloadErrors.targetCardId").isArray(),
                            jsonPath("$._embedded.payloadErrors.destinationColumnId").isArray()
                    );
        }

        @Test
        void unknownTargetCardShouldBadRequest() throws Exception {
            var payload = """
                    {
                        "targetCardId": "64393703-6a7a-4992-b2a3-6a4cea6de646",
                        "destinationColumnId": "89143482-fdbc-47fa-9a60-fca63335521f"
                    }
                    """;

            mockMvc.perform(post("/api/boards/%s/move-card".formatted(BOARD_ID))
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.message").value("Invalid payload."),
                            jsonPath("$._embedded.payloadErrors.targetCardId").isArray(),
                            jsonPath("$._embedded.payloadErrors.destinationColumnId").doesNotExist()
                    );
        }

        @Test
        void sameColumnShouldBadRequest() throws Exception {
            var payload = """
                    {
                        "targetCardId": "d8355640-cf9c-45ec-a1ee-398157f5a544",
                        "destinationColumnId": "7ab312f3-2661-4de4-9755-42d194c253c2"
                    }
                    """;

            mockMvc.perform(post("/api/boards/%s/move-card".formatted(BOARD_ID))
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.message").value("Invalid payload."),
                            jsonPath("$._embedded.payloadErrors.targetCardId").isArray(),
                            jsonPath("$._embedded.payloadErrors.destinationColumnId").isArray()
                    );
        }

        @Test
        void unknownColumnShouldBadRequest() throws Exception {
            var payload = """
                    {
                        "targetCardId": "d8355640-cf9c-45ec-a1ee-398157f5a544",
                        "destinationColumnId": "35413f08-9ff2-4d4d-ad13-af505b5b1c3c"
                    }
                    """;

            mockMvc.perform(post("/api/boards/%s/move-card".formatted(BOARD_ID))
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.message").value("Invalid payload."),
                            jsonPath("$._embedded.payloadErrors.targetCardId").doesNotExist(),
                            jsonPath("$._embedded.payloadErrors.destinationColumnId").isArray()
                    );
        }

        @Test
        void byUnknownIdShouldNotFound() throws Exception {
            var payload = """
                    {
                        "targetCardId": "d8355640-cf9c-45ec-a1ee-398157f5a544",
                        "destinationColumnId": "89143482-fdbc-47fa-9a60-fca63335521f"
                    }
                    """;

            mockMvc.perform(post("/api/boards/75d46c19-d28e-4a8d-8e7c-19220b15c507/move-card")
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byInvalidUuidShouldNotFound() throws Exception {
            var payload = """
                    {
                        "targetCardId": "d8355640-cf9c-45ec-a1ee-398157f5a544",
                        "destinationColumnId": "89143482-fdbc-47fa-9a60-fca63335521f"
                    }
                    """;

            mockMvc.perform(post("/api/boards/invalid-uuid/move-card")
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byUnownedBoardIdShouldNotFound() throws Exception {
            var payload = """
                    {
                        "targetCardId": "d8355640-cf9c-45ec-a1ee-398157f5a544",
                        "destinationColumnId": "89143482-fdbc-47fa-9a60-fca63335521f"
                    }
                    """;

            mockMvc.perform(post("/api/boards/7e885910-1df0-4744-8083-73e1d9769062/move-card")
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isNotFound());
        }
    }

}
