package io.github.stackpan.tasque.http;

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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(TestContainersConfig.class)
@AutoConfigureMockMvc
public class AuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @Sql(scripts = {"classpath:datasources/user.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
    public class Login {

        @Test
        void withUsernameIdentityShouldReturnJwt() throws Exception {
            var payload = """
                    {
                        "identity": "firstone",
                        "secret": "firstone-Secret123!"
                    }
                    """;

            mockMvc.perform(post("/auth/login")
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
                            jsonPath("$._links.me.href").value(containsString("/auth/me"))
                    );
        }

        @Test
        void withEmailIdentityShouldReturnJwt() throws Exception {
            var payload = """
                    {
                        "identity": "firstone@example.com",
                        "secret": "firstone-Secret123!"
                    }
                    """;

            mockMvc.perform(post("/auth/login")
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
                            jsonPath("$._links.me.href").value(containsString("/auth/me"))
                    );
        }

        @Test
        void withInvalidPayloadShouldBadRequest() throws Exception {
            var payload = """
                    {
                        "identity": 90
                    }
                    """;

            mockMvc.perform(post("/auth/login")
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

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(ExtMediaType.APPLICATION_HAL_JSON_VALUE)
                            .content(payload)
                    )
                    .andExpect(status().isUnauthorized());
        }
    }
}
