package io.github.stackpan.tasque.http;

import io.github.stackpan.tasque.TestContainersConfig;
import io.github.stackpan.tasque.UserMocks;
import io.github.stackpan.tasque.util.ExtMediaType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

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
}
