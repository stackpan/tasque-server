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
    class ListCardInAColumn {

        @Test
        void shouldReturnListOfCard() throws Exception {
            mockMvc.perform(get("/api/boards/%s/columns/%s/cards".formatted(BOARD_ID, COLUMN_ID))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                    )
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$._embedded.cards.length()").value(2),
                            jsonPath("$._embedded.cards[*].id").value(
                                    containsInAnyOrder("d8355640-cf9c-45ec-a1ee-398157f5a544", "a6d37ebb-6b9f-4c1c-be74-2a1485178ba8")
                            ),
                            jsonPath("$._embedded.cards[*].body").value(
                                    containsInAnyOrder("Card 111", "Card 112")
                            ),
                            jsonPath("$._embedded.cards[*].colorHex").value(
                                    containsInAnyOrder("#ffffff", "#ffffff")
                            ),
                            jsonPath("$._embedded.cards[*].createdAt").value(
                                    containsInAnyOrder("2024-07-28T00:00:00Z", "2024-07-28T00:00:01Z")
                            ),
                            jsonPath("$._embedded.cards[*].updatedAt").value(
                                    containsInAnyOrder("2024-07-28T00:00:00Z", "2024-07-28T00:00:01Z")
                            ),
                            jsonPath("$._embedded.cards[*]._links.boards.href", everyItem(containsString("/api/boards"))),
                            jsonPath("$._embedded.cards[*]._links.board.href", everyItem(containsString("/api/boards/%s".formatted(BOARD_ID)))),
                            jsonPath("$._embedded.cards[*]._links.columns.href", everyItem(containsString("/api/boards/%s/columns".formatted(BOARD_ID)))),
                            jsonPath("$._embedded.cards[*]._links.column.href", everyItem(containsString("/api/boards/%s/columns/%s".formatted(BOARD_ID, COLUMN_ID)))),
                            jsonPath("$._embedded.cards[*]._links.cards.href", everyItem(containsString("/api/boards/%s/columns/%s/cards".formatted(BOARD_ID, COLUMN_ID)))),
                            jsonPath("$._embedded.cards[*]._links.self.href").value(
                                    containsInAnyOrder(
                                            containsString("/api/boards/%s/columns/%s/cards/%s".formatted(BOARD_ID, COLUMN_ID, "d8355640-cf9c-45ec-a1ee-398157f5a544")),
                                            containsString("/api/boards/%s/columns/%s/cards/%s".formatted(BOARD_ID, COLUMN_ID, "a6d37ebb-6b9f-4c1c-be74-2a1485178ba8"))
                                    )
                            ),
                            jsonPath("$._links.boards.href").value(containsString("/api/boards")),
                            jsonPath("$._links.board.href").value(containsString("/api/boards/%s".formatted(BOARD_ID))),
                            jsonPath("$._links.columns.href").value(containsString("/api/boards/%s/columns".formatted(BOARD_ID))),
                            jsonPath("$._links.column.href").value(containsString("/api/boards/%s/columns/%s".formatted(BOARD_ID, COLUMN_ID))),
                            jsonPath("$._links.self.href").value(containsString("/api/boards/%s/columns/%s/cards".formatted(BOARD_ID, COLUMN_ID)))
                    );
        }

        @Test
        void byUnknownParentIdsShouldNotFound() throws Exception {
            mockMvc.perform(get("/api/boards/%s/columns/%s/cards".formatted("684fc4c1-0d7f-4b7e-b468-6e7f045adcf1", "75f6744c-9fa9-41ae-8ef7-2acff0bc6a5d"))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void byUnownedBoardShouldNotFound() throws Exception {
            mockMvc.perform(get("/api/boards/%s/columns/%s/cards".formatted("7e885910-1df0-4744-8083-73e1d9769062", "75f6744c-9fa9-41ae-8ef7-2acff0bc6a5d"))
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
    class CreateCard {

        @Test
        void shouldReturnCreatedCardAndStoredInDatabase() throws Exception {
            var payload = """
                    {
                        "body": "Created Card Test",
                        "colorHex": "#ffffff"
                    }
                    """;

            mockMvc.perform(post("/api/boards/%s/columns/%s/cards".formatted(BOARD_ID, COLUMN_ID))
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
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpect(header().string(HttpHeaders.LOCATION, matchesPattern("^.*/api/boards/%s/columns/%s/cards/%s".formatted(BOARD_ID, COLUMN_ID, Regexps.UUID))))
                    .andExpectAll(
                            jsonPath("$.id", matchesPattern(Regexps.UUID)),
                            jsonPath("$.body").value(JsonPath.<String>read(payload, "$.body")),
                            jsonPath("$.colorHex").value(JsonPath.<String>read(payload, "$.colorHex")),
                            jsonPath("$.createdAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$.updatedAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$._links.boards.href").value(containsString("/api/boards")),
                            jsonPath("$._links.board.href").value(containsString("/api/boards/%s".formatted(BOARD_ID))),
                            jsonPath("$._links.columns.href").value(containsString("/api/boards/%s/columns".formatted(BOARD_ID))),
                            jsonPath("$._links.column.href").value(containsString("/api/boards/%s/columns/%s".formatted(BOARD_ID, COLUMN_ID))),
                            jsonPath("$._links.cards.href").value(containsString("/api/boards/%s/columns/%s/cards".formatted(BOARD_ID, COLUMN_ID))),
                            jsonPath("$._links.self.href", matchesPattern("^.*/api/boards/%s/columns/%s/cards/%s".formatted(BOARD_ID, COLUMN_ID, Regexps.UUID)))
                    ).andDo(result -> {
                        var responseBody = result.getResponse().getContentAsString();

                        var createdId = UUID.fromString(JsonPath.read(responseBody, "$.id"));
                        var count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cards WHERE id = ?", Integer.class, createdId);

                        assertEquals(1, count);
                    });
        }

        @Test
        void withInvalidPayloadShouldBadRequest() throws Exception {
            var payload = """
                    {
                        "body": 9000
                    }
                    """;

            mockMvc.perform(post("/api/boards/%s/columns/%s/cards".formatted(BOARD_ID, COLUMN_ID))
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
                            jsonPath("$._embedded.payloadErrors.body").isArray(),
                            jsonPath("$._embedded.payloadErrors.colorHex").doesNotExist()
                    );
        }

        @Test
        void byUnknownParentIdsShouldNotFound() throws Exception {
            var payload = """
                    {
                        "body": "Created Card Test",
                        "colorHex": "#ffffff"
                    }
                    """;

            mockMvc.perform(post("/api/boards/%s/columns/%s/cards".formatted("684fc4c1-0d7f-4b7e-b468-6e7f045adcf1", "75f6744c-9fa9-41ae-8ef7-2acff0bc6a5d"))
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
        void byUnownedBoardShouldNotFound() throws Exception {
            var payload = """
                    {
                        "body": "Created Card Test",
                        "colorHex": "#ffffff"
                    }
                    """;

            mockMvc.perform(post("/api/boards/%s/columns/%s/cards".formatted("7e885910-1df0-4744-8083-73e1d9769062", "75f6744c-9fa9-41ae-8ef7-2acff0bc6a5d"))
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
                            jsonPath("$._links.cards.href").value(containsString("/api/boards/%s/columns/%s/cards".formatted(BOARD_ID, COLUMN_ID))),
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

    @Nested
    class UpdateCard {

        private final String CARD_ID = "d8355640-cf9c-45ec-a1ee-398157f5a544";

        @Test
        void shouldReturnUpdatedCardAndChangedInDatabase() throws Exception {
            var oldCard = jdbcTemplate.queryForMap("SELECT * FROM cards WHERE id = ?", UUID.fromString(CARD_ID));

            var payload = """
                    {
                        "body": "Updated Card",
                        "colorHex": "#000000"
                    }
                    """;

            mockMvc.perform(put("/api/boards/%s/columns/%s/cards/%s".formatted(BOARD_ID, COLUMN_ID, CARD_ID))
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
                            jsonPath("$.id").value(CARD_ID),
                            jsonPath("$.body").value(JsonPath.<String>read(payload, "$.body")),
                            jsonPath("$.colorHex").value(JsonPath.<String>read(payload, "$.colorHex")),
                            jsonPath("$.createdAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$.updatedAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$._links.boards.href").value(containsString("/api/boards")),
                            jsonPath("$._links.board.href").value(containsString("/api/boards/%s".formatted(BOARD_ID))),
                            jsonPath("$._links.columns.href").value(containsString("/api/boards/%s/columns".formatted(BOARD_ID))),
                            jsonPath("$._links.column.href").value(containsString("/api/boards/%s/columns/%s".formatted(BOARD_ID, COLUMN_ID))),
                            jsonPath("$._links.cards.href").value(containsString("/api/boards/%s/columns/%s/cards".formatted(BOARD_ID, COLUMN_ID))),
                            jsonPath("$._links.self.href").value(containsString("/api/boards/%s/columns/%s/cards/%s".formatted(BOARD_ID, COLUMN_ID, CARD_ID)))
                    );

            var updatedCard = jdbcTemplate.queryForMap("SELECT * FROM cards WHERE id = ?", UUID.fromString(CARD_ID));

            assertEquals(updatedCard.get("body"), JsonPath.<String>read(payload, "$.body"));
            assertEquals(updatedCard.get("color_hex"), JsonPath.<String>read(payload, "$.colorHex"));

            var oldUpdatedAt = ((Timestamp) oldCard.get("updated_at")).toInstant();
            var updatedUpdatedAt = ((Timestamp) updatedCard.get("updated_at")).toInstant();
            assertTrue(updatedUpdatedAt.isAfter(oldUpdatedAt));
        }

        @Test
        void withInvalidPayloadShouldBadRequest() throws Exception {
            var payload = """
                    {
                        "body": 9000
                    }
                    """;

            mockMvc.perform(put("/api/boards/%s/columns/%s/cards/%s".formatted(BOARD_ID, COLUMN_ID, CARD_ID))
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
                            jsonPath("$._embedded.payloadErrors.body").isArray(),
                            jsonPath("$._embedded.payloadErrors.colorHex").doesNotExist()
                    );
        }

        @Test
        void withInvalidUuidShouldReturnNotFound() throws Exception {
            var payload = """
                    {
                        "body": "Updated Card"
                        "colorHex": "#000000"
                    }
                    """;

            mockMvc.perform(put("/api/boards/%s/columns/%s/cards/%s".formatted(BOARD_ID, COLUMN_ID, "invaliduuid"))
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
        void withUnknownIdShouldReturnNotFound() throws Exception {
            var payload = """
                    {
                        "body": "Updated Card",
                        "colorHex": "#000000"
                    }
                    """;

            mockMvc.perform(put("/api/boards/%s/columns/%s/cards/%s".formatted(BOARD_ID, COLUMN_ID, "97f21e9e-09bc-4aa2-a929-49387fcde4e1"))
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
        void withUnownedBoardShouldReturnNotFound() throws Exception {
            var payload = """
                    {
                        "body": "Updated Card",
                        "colorHex": "#000000"
                    }
                    """;

            mockMvc.perform(put("/api/boards/%s/columns/%s/cards/%s".formatted("7e885910-1df0-4744-8083-73e1d9769062", COLUMN_ID, "97f21e9e-09bc-4aa2-a929-49387fcde4e1"))
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
    class DeleteCard {

        @Test
        void shouldNoContentAndMissingFromDatabase() throws Exception {
            final var CARD_ID = "d8355640-cf9c-45ec-a1ee-398157f5a544";

            mockMvc.perform(delete("/api/boards/%s/columns/%s/cards/%s".formatted(BOARD_ID, COLUMN_ID, CARD_ID))
                            .with(jwt().jwt(jwt -> jwt
                                            .claim("sub", "172e7077-76a4-4fa3-879d-6ec767c655e6")
                                            .claim("scope", "ROLE_USER")
                                    )
                            )
                    )
                    .andExpect(status().isNoContent());

            var count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cards WHERE id = ?", Integer.class, UUID.fromString(CARD_ID));
            assertEquals(0, count);
        }

        @Test
        void withInvalidUuidShouldReturnNotFound() throws Exception {
            mockMvc.perform(delete("/api/boards/%s/columns/%s/cards/%s".formatted(BOARD_ID, COLUMN_ID, "invaliduuid"))
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
            mockMvc.perform(delete("/api/boards/%s/columns/%s/cards/%s".formatted(BOARD_ID, COLUMN_ID, "97f21e9e-09bc-4aa2-a929-49387fcde4e1"))
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
            mockMvc.perform(delete("/api/boards/%s/columns/%s/cards/%s".formatted("7e885910-1df0-4744-8083-73e1d9769062", COLUMN_ID, "97f21e9e-09bc-4aa2-a929-49387fcde4e1"))
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
