
package ma.youcode.affiliatlink.profile;

import ma.youcode.affiliatlink.profile.entity.enums.ProfileStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import ma.youcode.affiliatlink.common.DuplicateEmailException;
import ma.youcode.affiliatlink.common.ResourceNotFoundException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdvertiserController.class)
class AdvertiserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfileService profileService;

    @Test
    void shouldCreateAdvertiserAndReturn201() throws Exception {
        AdvertiserResponse response = new AdvertiserResponse(
                1L,
                "company@test.com",
                ProfileStatus.PENDING,
                LocalDateTime.now(),
                "My Company",
                "Technology",
                "https://company.com"
        );

        when(profileService.createAdvertiser(any(AdvertiserCreateRequest.class)))
                .thenReturn(response);

        String requestBody = """
                {
                  "email": "company@test.com",
                  "companyName": "My Company",
                  "sector": "Technology",
                  "website": "https://company.com"
                }
                """;
        mockMvc.perform(post("/api/advertisers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("company@test.com"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.companyName").value("My Company"));
    }

    @Test
    void shouldRejectInvalidAdvertiserAndReturn400() throws Exception {
        String requestBody = """
            {
              "email": "invalid-email",
              "companyName": "",
              "sector": "Technology",
              "website": "https://company.com"
            }
            """;

        mockMvc.perform(post("/api/advertisers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }


    @Test
    void shouldRejectDuplicateAdvertiserEmailAndReturn409() throws Exception {
        String requestBody = """
            {
              "email": "existing@test.com",
              "companyName": "My Company",
              "sector": "Technology",
              "website": "https://company.com"
            }
            """;

        when(profileService.createAdvertiser(any(AdvertiserCreateRequest.class)))
                .thenThrow(new DuplicateEmailException("existing@test.com"));
        mockMvc.perform(post("/api/advertisers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void shouldReturn404WhenAdvertiserNotFound() throws Exception {

        when(profileService.findAdvertiserById(999L))
                .thenThrow(new ResourceNotFoundException("Advertiser not found"));

        mockMvc.perform(get("/api/advertisers/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Advertiser not found"));
    }

}
