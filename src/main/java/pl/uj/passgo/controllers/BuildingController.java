package pl.uj.passgo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.uj.passgo.models.Building;
import pl.uj.passgo.models.DTOs.buildingRequests.BuildingRequest;
import pl.uj.passgo.models.responses.BuidlingResponse;
import pl.uj.passgo.models.responses.FullBuildingResponse;
import pl.uj.passgo.services.BuildingService;

import java.util.List;

@RestController
@RequestMapping("/buildings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping
    public ResponseEntity<List<BuidlingResponse>> getBuildings(@RequestParam(required = false) Boolean approved) {
        List<BuidlingResponse> buildings = buildingService.getAllBuildings(approved);
        return ResponseEntity.ok(buildings);
    }

    @PostMapping
    public ResponseEntity<BuidlingResponse> createBuilding(@RequestBody BuildingRequest building) {
        BuidlingResponse createdBuilding = buildingService.createBuilding(building);
        return new ResponseEntity<>(createdBuilding, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FullBuildingResponse> getBuildingById(@PathVariable Long id) {
        FullBuildingResponse fetchedBuilding = buildingService.getFullBuidlingById(id);
        return ResponseEntity.ok(fetchedBuilding);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<BuidlingResponse> approveBuilding(@PathVariable Long id){
        BuidlingResponse approvedBuilding = buildingService.approveBuilding(id);
        return ResponseEntity.ok(approvedBuilding);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBuilding(@PathVariable Long id){
        buildingService.deleteBuilding(id);
        return ResponseEntity.ok(String.format("Buidling with id: %d was succesfully deleted", id));
    }

}
