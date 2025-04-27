package pl.uj.passgo.models.DTOs;



import java.time.LocalDate;

public record FaqRequest(String question, String answer, LocalDate addDate) {
}
