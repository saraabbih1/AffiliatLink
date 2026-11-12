
package ma.youcode.affiliatlink.profile;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ma.youcode.affiliatlink.profile.entity.enums.Niche;
import ma.youcode.affiliatlink.profile.entity.enums.ProfileStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.Optional;
import ma.youcode.affiliatlink.common.DuplicateEmailException;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
class ProfileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfileService profileService;


    @Test
    void shouldCreatePublisherAndReturn201() throws Exception {


        PublisherResponse response = new PublisherResponse(
                1L,
                "publisher@test.com",
                ProfileStatus.PENDING,
                java.time.LocalDateTime.now(),
                "Sara",
                "Abbih",
                Niche.TECH,
                "Tech content creator",
                "Morocco"
        );

        when(profileService.createPublisher(any(PublisherCreateRequest.class)))
                .thenReturn(response);

        String requestBody = """
            {
              "email": "publisher@test.com",
              "firstName": "Sara",
              "lastName": "Abbih",
              "niche": "TECH",
              "bio": "Tech content creator",
              "country": "Morocco"
            }
            """;


        mockMvc.perform(MockMvcRequestBuilders.post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("publisher@test.com"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldRejectInvalidPublisherAndReturn400() throws Exception {


        String requestBody = """
            {
              "email": "invalid-email",
              "firstName": "",
              "lastName": "Abbih",
              "niche": "TECH",
              "country": "Morocco"
            }
            """;


        mockMvc.perform(MockMvcRequestBuilders.post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void shouldRejectDuplicateEmailAndReturn409() throws Exception {

        String requestBody = """
            {
              "email": "existing@test.com",
              "firstName": "Sara",
              "lastName": "Abbih",
              "niche": "TECH",
              "country": "Morocco"
            }
            """;

        when(profileService.createPublisher(any(PublisherCreateRequest.class)))
                .thenThrow(new DuplicateEmailException("existing@test.com"));


        mockMvc.perform(MockMvcRequestBuilders.post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void shouldReturn404WhenPublisherNotFound() throws Exception {

        when(profileService.findPublisherById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/publishers/999"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Publisher not found"));
    }

}

