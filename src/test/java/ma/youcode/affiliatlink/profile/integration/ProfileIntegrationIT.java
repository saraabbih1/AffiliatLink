package ma.youcode.affiliatlink.profile.integration;

import ma.youcode.affiliatlink.config.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import ma.youcode.affiliatlink.profile.ProfileRepository;
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ProfileIntegrationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void shouldCreateAdvertiserSuccessfully() throws Exception {

        String email = "integration-advertiser@test.com";

        String json = """
                {
                    "email": "integration-advertiser@test.com",
                    "companyName": "Integration Company",
                    "sector": "Technology",
                    "website": "https://example.com"
                }
                """;

        mockMvc.perform(post("/api/advertisers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email));

        assertThat(profileRepository.findByEmail(email))
                .isPresent();
    }
    @Test
    void shouldRejectDuplicateEmail() throws Exception {
        String json = """
            {
                "email": "duplicate-integration@test.com",
                "companyName": "Test Company",
                "sector": "Technology"
            }
            """;
        mockMvc.perform(post("/api/advertisers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/advertisers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }
    @Test
    void shouldRetrieveAdvertiserById() throws Exception {
        String json = """
            {
                "email": "get-advertiser@test.com",
                "companyName": "Get Test Company",
                "sector": "Technology"
            }
            """;

        String response = mockMvc.perform(post("/api/advertisers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();


        Long id = profileRepository.findByEmail("get-advertiser@test.com")
                .orElseThrow()
                .getId();


        mockMvc.perform(get("/api/advertisers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("get-advertiser@test.com"));
    }
}