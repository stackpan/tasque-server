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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Nested
    class GetTeams {

        @Test
        void shouldReturnListOfJoinedTeams() throws Exception {
            mockMvc.perform(get("/api/teams").with(UserMocks.rizkyJwt()))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$._embedded.teams.length()").value(2),
                            jsonPath("$._embedded.teams[*].id").value(
                                    containsInAnyOrder("a8119215-c4cc-446a-808b-ff28c2ee9f3c", "1ce806d6-368a-48bc-8a24-394c78f3a568")
                            ),
                            jsonPath("$._embedded.teams[*].name").value(
                                    containsInAnyOrder("Team 1", "Team 2")
                            ),
                            jsonPath("$._embedded.teams[*].description").value(
                                    containsInAnyOrder("Team 1 description", "Team 2 description")
                            ),
                            jsonPath("$._embedded.teams[*].profilePictureUrl").value(
                                    containsInAnyOrder(null, (String) null)
                            ),
                            jsonPath("$._embedded.teams[*].createdAt").value(
                                    containsInAnyOrder("2024-07-28T00:00:00Z", "2024-07-28T00:00:01Z")
                            ),
                            jsonPath("$._embedded.teams[*].updatedAt").value(
                                    containsInAnyOrder("2024-07-28T00:00:00Z", "2024-07-28T00:00:01Z")
                            ),
                            jsonPath("$._embedded.teams[*]._links.self.href").value(
                                    containsInAnyOrder(
                                            containsString("/teams/%s".formatted("a8119215-c4cc-446a-808b-ff28c2ee9f3c")),
                                            containsString("/teams/%s".formatted("1ce806d6-368a-48bc-8a24-394c78f3a568"))
                                    )
                            ),
//                            jsonPath("$._embedded.teams[*]._links.upload.href").value(
//                                    containsInAnyOrder(
//                                            containsString("/teams/%s/upload".formatted("a8119215-c4cc-446a-808b-ff28c2ee9f3c")),
//                                            containsString("/teams/%s/upload".formatted("1ce806d6-368a-48bc-8a24-394c78f3a568"))
//                                    )
//                            ),
//                            jsonPath("$._embedded.teams[*]._links.transferOwnership.href").value(
//                                    containsInAnyOrder(
//                                            containsString("/teams/%s/transfer-ownership".formatted("a8119215-c4cc-446a-808b-ff28c2ee9f3c")),
//                                            containsString("/teams/%s/transfer-ownership".formatted("1ce806d6-368a-48bc-8a24-394c78f3a568"))
//                                    )
//                            ),
//                            jsonPath("$._embedded.teams[*]._links.members.href").value(
//                                    containsInAnyOrder(
//                                            containsString("/teams/%s/members".formatted("a8119215-c4cc-446a-808b-ff28c2ee9f3c")),
//                                            containsString("/teams/%s/members".formatted("1ce806d6-368a-48bc-8a24-394c78f3a568"))
//                                    )
//                            ),
                            jsonPath("$._links.self.href").value(containsString("/teams"))
                    );
        }
    }

    @Nested
    class CreateTeam {

        @Test
        void shouldCreatedAndReturnCreatedTeamAndStoredInDatabase() throws Exception {
            var payload = """
                    {
                      "name": "Team Test",
                      "description": "Sample description."
                    }
                    """;

            mockMvc.perform(post("/api/teams")
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpect(header().string(HttpHeaders.LOCATION, matchesPattern("^.*/api/teams/" + Regexps.UUID)))
                    .andExpectAll(
                            jsonPath("$.id", matchesPattern(Regexps.UUID)),
                            jsonPath("name").value("Team Test"),
                            jsonPath("description").value("Sample description."),
                            jsonPath("profilePictureUrl").isEmpty(),
                            jsonPath("$.createdAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$.updatedAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$._links.self.href", matchesPattern("^.*/api/teams/" + Regexps.UUID))
                    ).andDo(result -> {
                        var responseContent = result.getResponse().getContentAsString();

                        var createdId = JsonPath.<String>read(responseContent, "$.id");
                        var count = jdbcTemplate.queryForObject("select count(*) from teams where id = ?", Integer.class, UUID.fromString(createdId));

                        assertEquals(1, count);
                    });
        }

        @Test
        void withNullablePayloadShouldCreatedAndStoredInDatabase() throws Exception {
            var payload = """
                    {
                      "name": "Team Test",
                      "description": null
                    }
                    """;

            mockMvc.perform(post("/api/teams")
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpect(header().string(HttpHeaders.LOCATION, matchesPattern("^.*/api/teams/" + Regexps.UUID)))
                    .andExpectAll(
                            jsonPath("$.id", matchesPattern(Regexps.UUID)),
                            jsonPath("name").value("Team Test"),
                            jsonPath("description").isEmpty(),
                            jsonPath("profilePictureUrl").isEmpty(),
                            jsonPath("$.createdAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$.updatedAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$._links.self.href", matchesPattern("^.*/api/teams/" + Regexps.UUID))
                    )
                    .andDo(result -> {
                        var responseContent = result.getResponse().getContentAsString();

                        var createdId = JsonPath.<String>read(responseContent, "$.id");
                        var count = jdbcTemplate.queryForObject("select count(*) from teams where id = ?", Integer.class, UUID.fromString(createdId));

                        assertEquals(count, 1);
                    });
        }


        @Test
        void withInvalidPayloadShouldBadRequest() throws Exception {
            var payload = """
                    {
                      "name": 999
                    }
                    """;

            mockMvc.perform(post("/api/teams")
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
                            jsonPath("$._embedded.payloadErrors.description").doesNotExist()
                    );
        }
    }

    @Nested
    class GetTeam {

        @Test
        void shouldReturnTeam() throws Exception {
            var targetId = "a8119215-c4cc-446a-808b-ff28c2ee9f3c";

            mockMvc.perform(get("/api/teams/%s".formatted(targetId)).with(UserMocks.rizkyJwt()))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("id").value(targetId),
                            jsonPath("name").value("Team 1"),
                            jsonPath("description").value("Team 1 description"),
                            jsonPath("profilePictureUrl").isEmpty(),
                            jsonPath("createdAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("updatedAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("_links.self.href").value(containsString("/teams/%s".formatted("a8119215-c4cc-446a-808b-ff28c2ee9f3c")))
//                            jsonPath("$._embedded.teams[*]._links.upload.href").value(
//                                    containsInAnyOrder(
//                                            containsString("/teams/%s/upload".formatted("a8119215-c4cc-446a-808b-ff28c2ee9f3c")),
//                                            containsString("/teams/%s/upload".formatted("1ce806d6-368a-48bc-8a24-394c78f3a568"))
//                                    )
//                            ),
//                            jsonPath("$._embedded.teams[*]._links.transferOwnership.href").value(
//                                    containsInAnyOrder(
//                                            containsString("/teams/%s/transfer-ownership".formatted("a8119215-c4cc-446a-808b-ff28c2ee9f3c")),
//                                            containsString("/teams/%s/transfer-ownership".formatted("1ce806d6-368a-48bc-8a24-394c78f3a568"))
//                                    )
//                            ),
//                            jsonPath("$._embedded.teams[*]._links.members.href").value(
//                                    containsInAnyOrder(
//                                            containsString("/teams/%s/members".formatted("a8119215-c4cc-446a-808b-ff28c2ee9f3c")),
//                                            containsString("/teams/%s/members".formatted("1ce806d6-368a-48bc-8a24-394c78f3a568"))
//                                    )
//                            ),
                    );
        }

        @Test
        void byUnknownIdShouldNotFound() throws Exception {
            mockMvc.perform(get("/api/teams/75d46c19-d28e-4a8d-8e7c-19220b15c507")
                            .with(UserMocks.rizkyJwt())
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byInvalidUuidShouldNotFound() throws Exception {
            mockMvc.perform(get("/api/teams/invaliduuid")
                            .with(UserMocks.rizkyJwt())
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byNotJoinTeamIdShouldNotFound() throws Exception {
            mockMvc.perform(get("/api/teams/2db2bcd6-0b6a-4db1-a285-7fd93058cf4d")
                            .with(UserMocks.rizkyJwt())
                    )
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class UpdateTeam {

        private final String TARGET_ID = "a8119215-c4cc-446a-808b-ff28c2ee9f3c";

        @Test
        void shouldReturnUpdatedTeamAndChangedOnDatabase() throws Exception {
            var oldTeamMap = jdbcTemplate.queryForMap("select * from teams where id = ?", UUID.fromString(TARGET_ID));

            var payload = """
                    {
                        "name": "Updated Team 1",
                        "description": "Updated Team 1 description."
                    }
                    """;

            mockMvc.perform(put("/api/teams/%s".formatted(TARGET_ID))
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("id").value(TARGET_ID),
                            jsonPath("name").value(JsonPath.<String>read(payload, "$.name")),
                            jsonPath("description").value(JsonPath.<String>read(payload, "$.description")),
                            jsonPath("profilePictureUrl").isEmpty(),
                            jsonPath("$.createdAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$.updatedAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("_links.self.href").value(containsString("/teams/%s".formatted("a8119215-c4cc-446a-808b-ff28c2ee9f3c")))
                    )
                    .andDo(result -> {
                        var updatedTeamMap = jdbcTemplate.queryForMap("select * from teams where id = ?", UUID.fromString(TARGET_ID));

                        assertEquals(updatedTeamMap.get("name"), JsonPath.<String>read(payload, "$.name"));
                        assertEquals(updatedTeamMap.get("description"), JsonPath.<String>read(payload, "$.description"));

                        var oldTeamUpdatedAt = ((Timestamp) oldTeamMap.get("updated_at")).toInstant();
                        var updatedTeamUpdatedAt = ((Timestamp) updatedTeamMap.get("updated_at")).toInstant();
                        assertTrue(updatedTeamUpdatedAt.isAfter(oldTeamUpdatedAt));
                    });
        }

        @Test
        void withNullablePayloadShouldReturnUpdatedTeamAndChangedOnDatabase() throws Exception {
            var oldTeamMap = jdbcTemplate.queryForMap("select * from teams where id = ?", UUID.fromString(TARGET_ID));

            var payload = """
                    {
                        "name": "Updated Team 1",
                        "description": null
                    }
                    """;

            mockMvc.perform(put("/api/teams/%s".formatted(TARGET_ID))
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("id").value(TARGET_ID),
                            jsonPath("name").value(JsonPath.<String>read(payload, "$.name")),
                            jsonPath("description").isEmpty(),
                            jsonPath("profilePictureUrl").isEmpty(),
                            jsonPath("$.createdAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$.updatedAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("_links.self.href").value(containsString("/teams/%s".formatted("a8119215-c4cc-446a-808b-ff28c2ee9f3c")))
                    )
                    .andDo(result -> {
                        var updatedTeamMap = jdbcTemplate.queryForMap("select * from teams where id = ?", UUID.fromString(TARGET_ID));

                        assertEquals(updatedTeamMap.get("name"), JsonPath.<String>read(payload, "$.name"));
                        assertEquals(updatedTeamMap.get("description"), JsonPath.<String>read(payload, "$.description"));

                        var oldTeamUpdatedAt = ((Timestamp) oldTeamMap.get("updated_at")).toInstant();
                        var updatedTeamUpdatedAt = ((Timestamp) updatedTeamMap.get("updated_at")).toInstant();
                        assertTrue(updatedTeamUpdatedAt.isAfter(oldTeamUpdatedAt));
                    });
        }

        @Test
        void withInvalidPayloadShouldBadRequest() throws Exception {
            var payload = """
                    {
                        "name": 999
                    }
                    """;

            mockMvc.perform(put("/api/teams/%s".formatted(TARGET_ID))
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
                            jsonPath("$._embedded.payloadErrors.description").doesNotExist()
                    );
        }

        @Test
        void byUnknownIdShouldNotFound() throws Exception {
            var payload = """
                    {
                        "name": "Updated Team 1",
                        "description": "Updated Team 1 description."
                    }
                    """;

            mockMvc.perform(put("/api/teams/75d46c19-d28e-4a8d-8e7c-19220b15c507")
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
                        "name": "Updated Team 1",
                        "description": "Updated Team 1 description."
                    }
                    """;

            mockMvc.perform(put("/api/teams/invaliduuid")
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byUnownedTeamIdShouldNotFound() throws Exception {
            var payload = """
                    {
                        "name": "Updated Team 1",
                        "description": "Updated Team 1 description."
                    }
                    """;

            mockMvc.perform(put("/api/teams/2db2bcd6-0b6a-4db1-a285-7fd93058cf4d")
                            .with(UserMocks.rizkyJwt())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isNotFound());
        }
    }


    @Nested
    class DeleteTeam {

        @Test
        void shouldNoContentAndFilledDeletedAtInDatabase() throws Exception {
            String TARGET_ID = "a8119215-c4cc-446a-808b-ff28c2ee9f3c";

            mockMvc.perform(delete("/api/teams/%s".formatted(TARGET_ID))
                            .with(UserMocks.rizkyJwt())
                    )
                    .andExpect(status().isNoContent());

            var count = jdbcTemplate.queryForObject("select count(*) from teams where id = ? and deleted_at is null", Integer.class, UUID.fromString(TARGET_ID));
            assertEquals(count, 0);
        }

        @Test
        void byUnknownIdShouldNotFound() throws Exception {
            mockMvc.perform(delete("/api/teams/75d46c19-d28e-4a8d-8e7c-19220b15c507")
                            .with(UserMocks.rizkyJwt())
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byInvalidUuidShouldNotFound() throws Exception {
            mockMvc.perform(delete("/api/teams/invaliduuid")
                            .with(UserMocks.rizkyJwt())
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byUnownedBoardIdShouldNotFound() throws Exception {
            mockMvc.perform(delete("/api/teams/2db2bcd6-0b6a-4db1-a285-7fd93058cf4d")
                            .with(UserMocks.rizkyJwt())
                    )
                    .andExpect(status().isNotFound());
        }
    }
}
