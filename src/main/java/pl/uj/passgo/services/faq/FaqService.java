package pl.uj.passgo.services.faq;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.DTOs.FaqRequest;
import pl.uj.passgo.models.Faq;
import pl.uj.passgo.models.responses.FaqResponse;
import pl.uj.passgo.repos.faq.FaqRepository;

import java.time.Clock;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FaqService {
    private final FaqRepository faqRepository;
    private final Clock clock;

    public Page<FaqResponse> getAllFaqs(Pageable pageable) {
        return faqRepository.findAll(pageable).map(faq -> new FaqResponse(faq.getId(), faq.getQuestion(), faq.getAnswer()));
    }

    public FaqResponse getFaqById(Long faqId) {
        Faq faq = faqRepository.getFaqById(faqId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Faq with id: %d not found", faqId)));
        return mapFaqToFaqResponse(faq);
    }

    public FaqResponse addFaq(FaqRequest faqRequest) {
        Faq faq = new Faq();
        faq.setQuestion(faqRequest.question());
        faq.setAnswer(faqRequest.answer());
        faq.setAddDate(LocalDate.now(clock));
        faq.setUpdateDate(LocalDate.now(clock));

        return mapFaqToFaqResponse(faqRepository.save(faq));
    }

    private FaqResponse mapFaqToFaqResponse(Faq faq) {
        return new FaqResponse(faq.getId(), faq.getQuestion(), faq.getAnswer());
    }

    public void deleteFaq(Long faqId) {
        faqRepository.deleteById(faqId);
    }

    public FaqResponse updateFaq(FaqRequest faqRequest, Long faqId) {
        Faq faq = faqRepository.findById(faqId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Faq with id: %d not found", faqId)));

        faq.setAnswer(faqRequest.answer());
        faq.setQuestion(faqRequest.question());
        faq.setUpdateDate(LocalDate.now(clock));

        return mapFaqToFaqResponse(faqRepository.save(faq));
    }
}
