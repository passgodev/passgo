package pl.uj.passgo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.uj.passgo.models.DTOs.statistics.FullStatsResponse;
import pl.uj.passgo.models.DTOs.statistics.StatsFilter;
import pl.uj.passgo.models.DTOs.statistics.StatsResponse;
import pl.uj.passgo.services.StatsService;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping
    public ResponseEntity<FullStatsResponse> getEventStats(
        @ModelAttribute StatsFilter filter,
        @PageableDefault Pageable pageable
    ){
        return ResponseEntity.ok(statsService.getEventsStats(filter, pageable));
    }

}
