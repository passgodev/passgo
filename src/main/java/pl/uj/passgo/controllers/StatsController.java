package pl.uj.passgo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.uj.passgo.models.DTOs.StatsResponse;
import pl.uj.passgo.services.StatsService;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/event/{id}")
    public ResponseEntity<StatsResponse> getEventStats(@PathVariable Long id){
        return ResponseEntity.ok(statsService.getEventStats(id));
    }

}
