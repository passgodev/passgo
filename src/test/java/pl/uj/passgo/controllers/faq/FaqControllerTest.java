package pl.uj.passgo.controllers.faq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.uj.passgo.models.DTOs.FaqRequest;
import pl.uj.passgo.models.responses.FaqResponse;
import pl.uj.passgo.services.faq.FaqService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class FaqControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FaqService faqService;

    @Test
    void shouldReturnFaqList_whenGetFaqsCalled() throws Exception {
        // Arrange
        Page<FaqResponse> page = new PageImpl<>(List.of(new FaqResponse(1L, "Q", "A")));
        when(faqService.getAllFaqs(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/faqs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].question").value("Q"))
                .andExpect(jsonPath("$.content[0].answer").value("A"));
    }

    @Test
    void shouldReturnFaqById_whenExists() throws Exception {
        // Arrange
        Long id = 1L;
        FaqResponse response = new FaqResponse(id, "Q", "A");
        when(faqService.getFaqById(id)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/faqs/{faqId}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question").value("Q"))
                .andExpect(jsonPath("$.answer").value("A"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void shouldCreateFaq_whenValidRequest() throws Exception {
        // Arrange
        FaqRequest request = new FaqRequest("Q", "A");
        FaqResponse response = new FaqResponse(1L, "Q", "A");
        when(faqService.addFaq(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/faqs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.question").value("Q"))
                .andExpect(jsonPath("$.answer").value("A"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void shouldUpdateFaq_whenExists() throws Exception {
        // Arrange
        Long id = 1L;
        FaqRequest request = new FaqRequest("Updated Q", "Updated A");
        FaqResponse response = new FaqResponse(id, "Updated Q", "Updated A");
        when(faqService.updateFaq(eq(request), eq(id))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/faqs/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question").value("Updated Q"))
                .andExpect(jsonPath("$.answer").value("Updated A"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void shouldDeleteFaq_whenExists() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/faqs/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(faqService).deleteFaq(1L);
    }
}