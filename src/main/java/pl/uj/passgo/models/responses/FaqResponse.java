package pl.uj.passgo.models.responses;

import java.time.LocalDate;


public record FaqResponse(
    String question,
    String answer,
    LocalDate addDate
) {}
