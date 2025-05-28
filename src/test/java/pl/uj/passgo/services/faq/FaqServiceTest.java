package pl.uj.passgo.services.faq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.DTOs.FaqRequest;
import pl.uj.passgo.models.Faq;
import pl.uj.passgo.models.responses.FaqResponse;
import pl.uj.passgo.repos.faq.FaqRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FaqServiceTest {
    @Mock
    private FaqRepository faqRepository;

    @Mock
    private Clock clock;

    @InjectMocks
    private FaqService faqService;

    private void setupFixedClock() {
        Instant fixedInstant = Instant.parse("2025-05-27T14:00:00Z");
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(ZoneId.of("Europe/Warsaw"));
    }

    @Test
    void shouldReturnPagedFaqs_whenGetAllFaqsCalled() {
        // Arrange
        setupFixedClock();
        Pageable pageable = PageRequest.of(0, 10);
        List<Faq> faqs = List.of(new Faq(1L, "Q1", "A1", LocalDate.now(clock), LocalDate.now(clock)));
        Page<Faq> faqPage = new PageImpl<>(faqs);
        when(faqRepository.findAll(pageable)).thenReturn(faqPage);

        // Act
        Page<FaqResponse> result = faqService.getAllFaqs(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Q1", result.getContent().getFirst().question());
        assertEquals("A1", result.getContent().getFirst().answer());
        verify(faqRepository).findAll(pageable);
    }

    @Test
    void shouldReturnFaq_whenGetFaqByIdExists() {
        // Arrange
        setupFixedClock();
        Long id = 1L;
        Faq faq = new Faq(id, "Q1", "A1", LocalDate.now(clock), LocalDate.now(clock));
        when(faqRepository.getFaqById(id)).thenReturn(Optional.of(faq));

        // Act
        FaqResponse result = faqService.getFaqById(id);

        // Assert
        assertEquals("Q1", result.question());
        assertEquals("A1", result.answer());
        verify(faqRepository).getFaqById(id);
    }

    @Test
    void shouldThrowException_whenFaqNotFoundById() {
        // Arrange
        Long id = 99L;
        when(faqRepository.getFaqById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> faqService.getFaqById(id));
    }

    @Test
    void shouldSaveAndReturnFaq_whenAddFaq() {
        // Arrange
        setupFixedClock();
        FaqRequest request = new FaqRequest("Q1", "A1");
        Faq savedFaq = new Faq(1L, "Q1", "A1", LocalDate.now(clock), LocalDate.now(clock));
        when(faqRepository.save(any(Faq.class))).thenReturn(savedFaq);

        // Act
        FaqResponse result = faqService.addFaq(request);

        // Assert
        assertEquals("Q1", result.question());
        assertEquals("A1", result.answer());
        verify(faqRepository).save(any(Faq.class));
    }

    @Test
    void shouldDeleteFaq_whenDeleteCalled() {
        // Arrange
        Long id = 1L;

        // Act
        faqService.deleteFaq(id);

        // Assert
        verify(faqRepository).deleteById(id);
    }

    @Test
    void shouldUpdateAndReturnFaq_whenFaqExists() {
        // Arrange
        setupFixedClock();
        Long id = 1L;
        Faq existingFaq = new Faq(id, "Old Q", "Old A", LocalDate.now(clock), LocalDate.now(clock));
        when(faqRepository.findById(id)).thenReturn(Optional.of(existingFaq));
        when(faqRepository.save(any(Faq.class))).thenAnswer(i -> i.getArgument(0));
        FaqRequest request = new FaqRequest("New Q", "New A");

        // Act
        FaqResponse result = faqService.updateFaq(request, id);

        // Assert
        assertEquals("New Q", result.question());
        assertEquals("New A", result.answer());
        verify(faqRepository).save(existingFaq);
    }

    @Test
    void shouldThrowException_whenUpdateFaqNotFound() {
        // Arrange
        Long id = 1L;
        FaqRequest request = new FaqRequest("Q", "A");
        when(faqRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> faqService.updateFaq(request, id));
    }
}