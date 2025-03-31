package pl.uj.passgo.models.DTOs.buildingRequests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.uj.passgo.models.DTOs.AddressRequest;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuildingRequest {
    private String name;
    private AddressRequest address;
    private List<SectorRequest> sectors;
}
