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


@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FaqService {
    private final FaqRepository faqRepository;

    public Page<FaqResponse> getAllFaqs(Pageable pageable) {
        return faqRepository.findAll(pageable).map(faq -> new FaqResponse(faq.getQuestion(), faq.getAnswer(), faq.getAddDate()));
    }

    public FaqResponse getFaqById(Long faqId) {
        return faqRepository.getFaqById(faqId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Faq with id: %d not found", faqId)));
    }

    public FaqResponse addFaq(FaqRequest faqRequest) {
        Faq faq = new Faq();
        faq.setQuestion(faqRequest.getQuestion());
        faq.setAnswer(faqRequest.getAnswer());
        faq.setAddDate(faqRequest.getAddDate());

        return mapFaqToFaqResponse(faqRepository.save(faq));
    }

    private FaqResponse mapFaqToFaqResponse(Faq save) {
        return new FaqResponse(save.getQuestion(), save.getAnswer(), save.getAddDate());
    }

    public void deleteFaq(Long faqId) {
        faqRepository.deleteById(faqId);
    }
}
