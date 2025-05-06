package pl.uj.passgo.models.responses;

import java.time.LocalDate;


public record FaqResponse(
    Long id,
    String question,
    String answer
) {}
