package pl.uj.passgo.models.DTOs.buildingRequests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SectorRequest {
    private String name;
    private List<RowRequest> rows;
    private Boolean standingArea;
}
