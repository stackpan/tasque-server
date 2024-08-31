package io.github.stackpan.tasque.http;

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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(TestContainersConfig.class)
@Sql(scripts = "classpath:datasources/user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
@Transactional
public class AuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class Login {

        @Test
        void withUsernameIdentityShouldReturnJwt() throws Exception {
            var payload = """
                    {
                        "identity": "rizky",
                        "secret": "rizky-Secret123!"
                    }
                    """;

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.token").isString(),
                            jsonPath("$.type").value("JWT"),
                            jsonPath("$.expiresAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$._links.me.href").value(containsString("/api/auth/me"))
                    );
        }

        @Test
        void withEmailIdentityShouldReturnJwt() throws Exception {
            var payload = """
                    {
                        "identity": "rizky@example.com",
                        "secret": "rizky-Secret123!"
                    }
                    """;

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.token").isString(),
                            jsonPath("$.type").value("JWT"),
                            jsonPath("$.expiresAt", matchesPattern(Regexps.TIMESTAMP)),
                            jsonPath("$._links.me.href").value(containsString("/api/auth/me"))
                    );
        }

        @Test
        void withInvalidPayloadShouldBadRequest() throws Exception {
            var payload = """
                    {
                        "identity": 90
                    }
                    """;

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.message").value("Invalid payload."),
                            jsonPath("$._embedded.payloadErrors.identity").isArray(),
                            jsonPath("$._embedded.payloadErrors.secret").isArray()
                    );
        }

        @Test
        void withInvalidCredentialsShouldUnauthorized() throws Exception {
            var payload = """
                    {
                        "identity": "invaliduser",
                        "secret": "invalidusercredentialSecret123!"
                    }
                    """;

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class Me {

        @Test
        void shouldReturnUser() throws Exception {
            mockMvc.perform(get("/api/auth/me").with(UserMocks.rizkyJwt()))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, ExtMediaType.APPLICATION_HAL_JSON_VALUE))
                    .andExpectAll(
                            jsonPath("$.id").value("172e7077-76a4-4fa3-879d-6ec767c655e6"),
                            jsonPath("$.username").value("rizky"),
                            jsonPath("$.email").value("rizky@example.com"),
                            jsonPath("$.firstName").value("Rizky"),
                            jsonPath("$.lastName").value("Anto"),
                            jsonPath("$.profilePictureUrl").isEmpty(),
                            jsonPath("$.emailVerifiedAt").isEmpty(),
                            jsonPath("$.createdAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$.updatedAt").value("2024-07-28T00:00:00Z"),
                            jsonPath("$._links.self.href").value(containsString("/api/auth/me")),
                            jsonPath("$._links.upload.href").value(containsString("/api/auth/me/upload")),
                            jsonPath("$._links.changePassword.href").value(containsString("/api/auth/me/change-password")),
                            jsonPath("$._links.user.href").value(containsString("/users/172e7077-76a4-4fa3-879d-6ec767c655e6"))
                    );
        }

        @Test
        void unauthenticatedShouldUnauthorized() throws Exception {
            mockMvc.perform(get("/api/auth/me"))
                    .andExpect(status().isUnauthorized());
        }
    }
}
