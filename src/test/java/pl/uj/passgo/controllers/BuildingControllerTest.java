package pl.uj.passgo.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.uj.passgo.models.DTOs.buildingRequests.BuildingRequest;
import pl.uj.passgo.models.Status;
import pl.uj.passgo.models.responses.building.BuildingResponse;
import pl.uj.passgo.models.responses.building.FullBuildingResponse;
import pl.uj.passgo.services.BuildingService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuildingControllerTest {

    @Mock
    private BuildingService buildingService;

    @InjectMocks
    private BuildingController buildingController;

    @Test
    void getBuildings_shouldReturnList() {
        List<BuildingResponse> mockList = List.of(
                new BuildingResponse(1L, "Budynek A", null, Status.APPROVED),
                new BuildingResponse(2L, "Budynek B", null, Status.PENDING)
        );

        when(buildingService.getAllBuildings(null)).thenReturn(mockList);

        ResponseEntity<List<BuildingResponse>> response = buildingController.getBuildings(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(buildingService).getAllBuildings(null);
    }

    @Test
    void createBuilding_shouldReturnCreated() {
        BuildingRequest request = BuildingRequest.builder()
                .name("Test")
                .address(null)
                .sectors(List.of())
                .build();

        BuildingResponse responseMock = new BuildingResponse(1L, "Test", null, Status.PENDING);

        when(buildingService.createBuilding(request)).thenReturn(responseMock);

        ResponseEntity<BuildingResponse> response = buildingController.createBuilding(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseMock, response.getBody());
        verify(buildingService).createBuilding(request);
    }

    @Test
    void updateBuildingStatus_shouldReturnUpdatedBuilding() {
        Long id = 1L;
        Status newStatus = Status.APPROVED;
        BuildingResponse updated = new BuildingResponse(id, "Test", null, newStatus);

        when(buildingService.updateBuildingStatus(id, newStatus)).thenReturn(updated);

        ResponseEntity<BuildingResponse> response = buildingController.updateBuildingStatus(id, newStatus);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newStatus, response.getBody().status());
        verify(buildingService).updateBuildingStatus(id, newStatus);
    }

    @Test
    void deleteBuilding_shouldReturnNoContent() {
        Long id = 1L;
        doNothing().when(buildingService).deleteBuilding(id);

        ResponseEntity<Void> response = buildingController.deleteBuilding(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(buildingService).deleteBuilding(id);
    }
}
