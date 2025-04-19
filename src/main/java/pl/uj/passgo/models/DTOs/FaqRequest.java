package pl.uj.passgo.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaqRequest {
    private String question;
    private String answer;
    private LocalDate addDate;
}
