
package ma.youcode.affiliatlink.profile;
import ma.youcode.affiliatlink.common.InvalidStatusTransitionException;
import ma.youcode.affiliatlink.profile.entity.enums.ProfileStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ma.youcode.affiliatlink.common.ResourceNotFoundException;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminProfileController.class)
class AdminProfileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfileService profileService;

    @Test
    void shouldUpdateProfileStatusAndReturn204() throws Exception {

        String requestBody = """
                {
                  "status": "ACTIVE"
                }
                """;


        mockMvc.perform(patch("/api/admin/profiles/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNoContent());


        verify(profileService)
                .updateProfileStatus(1L, ProfileStatus.ACTIVE);
    }

    @Test
    void shouldReturn404WhenProfileNotFound() throws Exception {

        doThrow(new ResourceNotFoundException("Profile not found"))
                .when(profileService)
                .updateProfileStatus(999L, ProfileStatus.ACTIVE);

        String requestBody = """
            {
              "status": "ACTIVE"
            }
            """;


        mockMvc.perform(patch("/api/admin/profiles/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Profile not found"));
    }

    @Test
    void shouldReturn409WhenStatusTransitionInvalid() throws Exception {

        doThrow(new InvalidStatusTransitionException(
                "Invalid status transition"))
                .when(profileService)
                .updateProfileStatus(1L, ProfileStatus.SUSPENDED);

        String requestBody = """
            {
              "status": "SUSPENDED"
            }
            """;


        mockMvc.perform(patch("/api/admin/profiles/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }


}
