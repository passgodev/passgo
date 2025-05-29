package pl.uj.passgo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.uj.passgo.models.Status;
import pl.uj.passgo.models.DTOs.buildingRequests.BuildingRequest;
import pl.uj.passgo.models.responses.building.BuildingResponse;
import pl.uj.passgo.models.responses.building.FullBuildingResponse;
import pl.uj.passgo.services.BuildingService;

import java.util.List;

@RestController
@RequestMapping("/buildings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping
    public ResponseEntity<List<BuildingResponse>> getBuildings(@RequestParam(required = false) Status status) {
        List<BuildingResponse> buildings = buildingService.getAllBuildings(status);
        return ResponseEntity.ok(buildings);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'ORGANIZER')")
    @PostMapping
    public ResponseEntity<BuildingResponse> createBuilding(@RequestBody BuildingRequest building) {
        BuildingResponse createdBuilding = buildingService.createBuilding(building);
        return new ResponseEntity<>(createdBuilding, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FullBuildingResponse> getBuildingById(@PathVariable Long id) {
        FullBuildingResponse fetchedBuilding = buildingService.getFullBuildingById(id);
        return ResponseEntity.ok(fetchedBuilding);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<BuildingResponse> updateBuildingStatus(@PathVariable Long id, @RequestParam Status status) {
        BuildingResponse updatedBuilding = buildingService.updateBuildingStatus(id, status);
        return ResponseEntity.ok(updatedBuilding);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long id){
        buildingService.deleteBuilding(id);
        return ResponseEntity.noContent().build();
    }

}
