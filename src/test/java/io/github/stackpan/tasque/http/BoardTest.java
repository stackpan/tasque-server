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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Import(TestContainersConfig.class)
@Sql(scripts = {"classpath:datasources/user.sql", "classpath:datasources/board.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
@Transactional
public class BoardTest {

    @Autowired
    private MockMvc mockMvc;

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
    }

}
