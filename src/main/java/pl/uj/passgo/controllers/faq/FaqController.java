package pl.uj.passgo.controllers.faq;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.uj.passgo.models.DTOs.FaqRequest;
import pl.uj.passgo.models.responses.FaqResponse;
import pl.uj.passgo.services.faq.FaqService;


@RestController
@RequestMapping("/faqs")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FaqController {
    private final FaqService faqService;

    @GetMapping
    public ResponseEntity<Page<FaqResponse>> getFaqs(@PageableDefault Pageable pageable) {
        var response = faqService.getAllFaqs(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{faqId}")
    public ResponseEntity<FaqResponse> getFaqById(@PathVariable Long faqId) {
        var response = faqService.getFaqById(faqId);
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<FaqResponse> addFaq(@RequestBody FaqRequest faqRequest){
        var response = faqService.addFaq(faqRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("{faqId}")
    public ResponseEntity<Void> deleteFaq(@RequestParam Long faqId) {
        faqService.deleteFaq(faqId);
        return ResponseEntity.noContent().build();
    }
}
