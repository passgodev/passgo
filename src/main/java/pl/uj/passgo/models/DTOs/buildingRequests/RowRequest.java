package pl.uj.passgo.models.DTOs.buildingRequests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RowRequest {
    private Long rowNumber;
    private Long seatsCount;
}
