package pl.uj.passgo.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.*;
import pl.uj.passgo.models.DTOs.AddressRequest;
import pl.uj.passgo.models.DTOs.buildingRequests.BuildingRequest;
import pl.uj.passgo.models.DTOs.buildingRequests.RowRequest;
import pl.uj.passgo.models.DTOs.buildingRequests.SectorRequest;
import pl.uj.passgo.models.responses.building.BuildingResponse;
import pl.uj.passgo.models.responses.building.FullBuildingResponse;
import pl.uj.passgo.repos.BuildingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BuildingServiceTest {

    @Mock
    private BuildingRepository buildingRepository;

    @InjectMocks
    private BuildingService buildingService;

    private Building sampleBuilding(){
        return Building.builder()
                .id(1L)
                .name("Tauron Arena")
                .address(
                        Address.builder()
                                .buildingNumber("1")
                                .id(1L)
                                .city("Kraków")
                                .country("Polska")
                                .postalCode("51-560")
                                .street("Dworcowa")
                                .build()
                )
                .sectors(
                        List.of(Sector.builder()
                                .id(1L)
                                .name("Sektor A")
                                .building(null)
                                .standingArea(false)
                                .rows(List.of())
                                .build()
                        )
                )
                .build();
    }

    private BuildingRequest createRequest(){
        AddressRequest addressRequest = AddressRequest.builder()
                .country("Polska")
                .city("Kraków")
                .street("Dworcowa")
                .postalCode("31-560")
                .buildingNumber("1")
                .build();

        RowRequest rowRequest = RowRequest.builder()
                .rowNumber(1L)
                .seatsCount(3L)
                .build();

        SectorRequest sectorRequest = SectorRequest.builder()
                .name("Sektor A")
                .standingArea(false)
                .rows(List.of(rowRequest))
                .build();

        return BuildingRequest.builder()
                .name("Tauron Arena")
                .address(addressRequest)
                .sectors(List.of(sectorRequest))
                .build();
    }

    @Test
    void getAllBuildings_shouldReturnAll_whenStatusIsNull() {
        List<Building> buildings = List.of(sampleBuilding());
        when(buildingRepository.findAll()).thenReturn(buildings);

        List<BuildingResponse> result = buildingService.getAllBuildings(null);

        assertEquals(1, result.size());
    }

    @Test
    void getAllBuildings_shouldReturnFiltered_whenStatusIsProvided() {
        Building building = sampleBuilding();
        building.setStatus(Status.APPROVED);
        when(buildingRepository.findByStatus(Status.APPROVED)).thenReturn(List.of(building));

        List<BuildingResponse> result = buildingService.getAllBuildings(Status.APPROVED);

        assertEquals(1, result.size());
        assertEquals(Status.APPROVED, result.get(0).status());
    }

    @Test
    void getBuildingById_shouldReturnBuilding_whenExists() {
        Building building = sampleBuilding();
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));

        Building result = buildingService.getBuildingById(1L);

        assertEquals("Tauron Arena", result.getName());
    }

    @Test
    void getBuildingById_shouldThrowWhenNotFound() {
        when(buildingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> buildingService.getBuildingById(1L));
    }

    @Test
    void deleteBuilding_shouldCallRepositoryDelete() {
        buildingService.deleteBuilding(1L);
        verify(buildingRepository).deleteById(1L);
    }

    @Test
    void updateBuildingStatus_shouldUpdateStatus() {
        Building building = sampleBuilding();
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(buildingRepository.save(any(Building.class))).thenAnswer(i -> i.getArgument(0));

        BuildingResponse response = buildingService.updateBuildingStatus(1L, Status.APPROVED);

        assertEquals(Status.APPROVED, response.status());
    }

    @Test
    void getFullBuildingById_shouldReturnCompleteStructure() {
        Building building = sampleBuilding();
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));

        FullBuildingResponse response = buildingService.getFullBuildingById(1L);

        assertEquals("Tauron Arena", response.name());
        assertEquals("Kraków", response.address().getCity());
        assertEquals(1, response.sectors().size());
    }

    @Test
    void createBuilding_shouldCreateAndReturnBuildingResponse() {
        BuildingRequest buildingRequest = createRequest();

        Building savedBuilding = new Building();
        savedBuilding.setId(1L);
        savedBuilding.setName(buildingRequest.getName());
        savedBuilding.setAddress(Address.builder()
                .country(buildingRequest.getAddress().getCountry())
                .city(buildingRequest.getAddress().getCity())
                .street(buildingRequest.getAddress().getStreet())
                .postalCode(buildingRequest.getAddress().getPostalCode())
                .buildingNumber(buildingRequest.getAddress().getBuildingNumber())
                .build());
        savedBuilding.setStatus(Status.PENDING);


        when(buildingRepository.save(any(Building.class))).thenReturn(savedBuilding);

        ArgumentCaptor<Building> buildingCaptor = ArgumentCaptor.forClass(Building.class);
        BuildingResponse response = buildingService.createBuilding(buildingRequest);
        verify(buildingRepository).save(buildingCaptor.capture());
        Building capturedBuilding = buildingCaptor.getValue();


        assertEquals("Tauron Arena", capturedBuilding.getName());

        assertNotNull(capturedBuilding.getAddress());
        assertEquals("Polska", capturedBuilding.getAddress().getCountry());
        assertEquals("Kraków", capturedBuilding.getAddress().getCity());
        assertEquals("Dworcowa", capturedBuilding.getAddress().getStreet());
        assertEquals("31-560", capturedBuilding.getAddress().getPostalCode());
        assertEquals("1", capturedBuilding.getAddress().getBuildingNumber());

        assertNotNull(capturedBuilding.getSectors());
        assertEquals(1, capturedBuilding.getSectors().size());

        Sector capturedSector = capturedBuilding.getSectors().get(0);
        assertEquals("Sektor A", capturedSector.getName());
        assertFalse(capturedSector.getStandingArea());

        assertNotNull(capturedSector.getRows());
        assertEquals(1, capturedSector.getRows().size());

        Row capturedRow = capturedSector.getRows().get(0);
        assertEquals(1L, capturedRow.getRowNumber());
        assertEquals(3L, capturedRow.getSeatsCount());

        assertNotNull(capturedRow.getSeats());
        assertEquals(3, capturedRow.getSeats().size());
        for (int i = 0; i < 3; i++) {
            assertEquals(i + 1, capturedRow.getSeats().get(i).getSeatNumber());
        }

        assertNotNull(response);
        assertEquals(savedBuilding.getId(), response.id());
        assertEquals("Tauron Arena", response.name());
        assertNotNull(response.address());
        assertEquals("Polska", response.address().country());
        assertEquals(Status.PENDING, response.status());
    }

}
