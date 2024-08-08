package io.github.stackpan.tasque.http;

import com.jayway.jsonpath.JsonPath;
import io.github.stackpan.tasque.TestContainersConfig;
import io.github.stackpan.tasque.util.ExtMediaType;
import io.github.stackpan.tasque.util.Regexps;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Import(TestContainersConfig.class)
@Sql(scripts = {"classpath:datasources/user.sql", "classpath:datasources/board.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(statements = {"delete from boards", "delete from users"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureMockMvc
public class BoardTest {

    private static final Logger log = LoggerFactory.getLogger(BoardTest.class);
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Nested
    class ListBoard {

        @Test
        void shouldReturnListOfBoard() throws Exception {
            mockMvc.perform(get("/boards")
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
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
                            jsonPath("$._embedded.boards[*]._embedded.owner.username", everyItem(equalTo("firstone"))),
                            jsonPath("$._embedded.boards[*]._embedded.owner.email", everyItem(equalTo("firstone@example.com"))),
                            jsonPath("$._embedded.boards[*]._embedded.owner.firstName", everyItem(equalTo("First"))),
                            jsonPath("$._embedded.boards[*]._embedded.owner.lastName", everyItem(equalTo("One"))),
                            jsonPath("$._embedded.boards[*]._embedded.owner.profilePictureUrl", everyItem(nullValue())),
                            jsonPath("$._embedded.boards[*]._embedded.owner.emailVerifiedAt", everyItem(nullValue())),
                            jsonPath("$._embedded.boards[*]._embedded.owner.createdAt", everyItem(equalTo("2024-07-28T00:00:00Z"))),
                            jsonPath("$._embedded.boards[*]._embedded.owner.updatedAt", everyItem(equalTo("2024-07-28T00:00:00Z"))),
                            jsonPath("$._embedded.boards[*]._embedded.owner._links.self.href", everyItem(containsString("/users/172e7077-76a4-4fa3-879d-6ec767c655e6"))),
                            jsonPath("$._links.self.href").value(containsString("/boards"))
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

            mockMvc.perform(post("/boards")
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpect(header().string(HttpHeaders.LOCATION, matchesPattern("^.*/boards/" + Regexps.UUID)))
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
                            jsonPath("$._embedded.owner.username").value("firstone"),
                            jsonPath("$._embedded.owner.email").value("firstone@example.com"),
                            jsonPath("$._embedded.owner.firstName").value("First"),
                            jsonPath("$._embedded.owner.lastName").value("One"),
                            jsonPath("$._embedded.owner.profilePictureUrl").isEmpty(),
                            jsonPath("$._embedded.owner.emailVerifiedAt").isEmpty(),
                            jsonPath("$._embedded.owner.createdAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._embedded.owner.updatedAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._embedded.owner._links.self.href").value(containsString("/users/172e7077-76a4-4fa3-879d-6ec767c655e6")),
                            jsonPath("$._links.boards.href").value(containsString("/boards")),
                            jsonPath("$._links.self.href", matchesPattern("^.*/boards/" + Regexps.UUID))
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

            mockMvc.perform(post("/boards")
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.message").value("Invalid payload."),
                            jsonPath("$._embedded.payloadErrors.name").isArray(),
                            jsonPath("$._embedded.payloadErrors.colorHex").isArray()
                    );
        }
    }

    @Nested
    class GetBoard {

        @Test
        void shouldReturnBoard() throws Exception {
            var targetId = "0eec62bb-e1b6-40d8-aa3e-349853b96b6e";

            mockMvc.perform(get("/boards/%s".formatted(targetId))
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
                            jsonPath("$.name").value("Board One"),
                            jsonPath("$.description").value("A long description of Board One."),
                            jsonPath("$.bannerPictureUrl").isEmpty(),
                            jsonPath("$.colorHex").value("#000000"),
                            jsonPath("$.ownerId").value("172e7077-76a4-4fa3-879d-6ec767c655e6"),
                            jsonPath("$.ownerType").value("USER"),
                            jsonPath("$.createdAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$.updatedAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._embedded.owner.id").value("172e7077-76a4-4fa3-879d-6ec767c655e6"),
                            jsonPath("$._embedded.owner.username").value("firstone"),
                            jsonPath("$._embedded.owner.email").value("firstone@example.com"),
                            jsonPath("$._embedded.owner.firstName").value("First"),
                            jsonPath("$._embedded.owner.lastName").value("One"),
                            jsonPath("$._embedded.owner.profilePictureUrl").isEmpty(),
                            jsonPath("$._embedded.owner.emailVerifiedAt").isEmpty(),
                            jsonPath("$._embedded.owner.createdAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._embedded.owner.updatedAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._embedded.owner._links.self.href").value(containsString("/users/172e7077-76a4-4fa3-879d-6ec767c655e6")),
                            jsonPath("$._links.boards.href").value(containsString("/boards")),
                            jsonPath("$._links.self.href").value(containsString("/boards/%s".formatted(targetId)))
                    );
        }

        @Test
        void byUnknownIdShouldNotFound() throws Exception {
            mockMvc.perform(get("/boards/75d46c19-d28e-4a8d-8e7c-19220b15c507")
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
            mockMvc.perform(get("/boards/invaliduuid")
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
            mockMvc.perform(get("/boards/7e885910-1df0-4744-8083-73e1d9769062")
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

            mockMvc.perform(put("/boards/%s".formatted(TARGET_ID))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
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
                            jsonPath("$._embedded.owner.username").value("firstone"),
                            jsonPath("$._embedded.owner.email").value("firstone@example.com"),
                            jsonPath("$._embedded.owner.firstName").value("First"),
                            jsonPath("$._embedded.owner.lastName").value("One"),
                            jsonPath("$._embedded.owner.profilePictureUrl").isEmpty(),
                            jsonPath("$._embedded.owner.emailVerifiedAt").isEmpty(),
                            jsonPath("$._embedded.owner.createdAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._embedded.owner.updatedAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._embedded.owner._links.self.href").value(containsString("/users/172e7077-76a4-4fa3-879d-6ec767c655e6")),
                            jsonPath("$._links.boards.href").value(containsString("/boards")),
                            jsonPath("$._links.self.href").value(containsString("/boards/%s".formatted(TARGET_ID)))
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
        void withInvalidPayloadShouldBadRequest() throws Exception {
            var payload = """
                    {
                        "name": 999,
                        "colorHex": "#fnffff"
                    }
                    """;

            mockMvc.perform(put("/boards/%s".formatted(TARGET_ID))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.message").value("Invalid payload."),
                            jsonPath("$._embedded.payloadErrors.name").isArray(),
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

            mockMvc.perform(put("/boards/75d46c19-d28e-4a8d-8e7c-19220b15c507")
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
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

            mockMvc.perform(put("/boards/invaliduuid")
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
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

            mockMvc.perform(get("/boards/7e885910-1df0-4744-8083-73e1d9769062")
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
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

            mockMvc.perform(delete("/boards/%s".formatted(TARGET_ID))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                    )
                    .andExpect(status().isNoContent());

            var count = jdbcTemplate.queryForObject("select count(*) from boards where id = ?", Integer.class, UUID.fromString(TARGET_ID));
            assertEquals(count, 0);
        }

        @Test
        void byUnknownIdShouldNotFound() throws Exception {
            mockMvc.perform(delete("/boards/75d46c19-d28e-4a8d-8e7c-19220b15c507")
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
            mockMvc.perform(delete("/boards/invaliduuid")
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
            mockMvc.perform(delete("/boards/7e885910-1df0-4744-8083-73e1d9769062")
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
