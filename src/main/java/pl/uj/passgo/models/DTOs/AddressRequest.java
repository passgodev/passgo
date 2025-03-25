package pl.uj.passgo.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {
    private String country;
    private String city;
    private String street;
    private String postalCode;
    private Long buildingNumber;
}
