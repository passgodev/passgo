package pl.uj.passgo.models.responses;

public record FaqResponse(
    Long id,
    String question,
    String answer
) {}
