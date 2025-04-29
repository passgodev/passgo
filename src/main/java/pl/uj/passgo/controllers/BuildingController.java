package pl.uj.passgo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.uj.passgo.models.Building;
import pl.uj.passgo.models.DTOs.buildingRequests.BuildingRequest;
import pl.uj.passgo.services.BuildingService;

import java.util.List;

@RestController
@RequestMapping("/buildings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping
    public ResponseEntity<List<Building>> getBuildings(@RequestParam(required = false) Boolean approved) {
        List<Building> buildings;

        if(approved == null){
            buildings = buildingService.getAllBuildings();
        }
        else{
            buildings = buildingService.getBuidlingsByApproved(approved) ;
        }

        return ResponseEntity.ok(buildings);
    }

    @PostMapping
    public ResponseEntity<Building> createBuilding(@RequestBody BuildingRequest building) {
        Building createdBuilding = buildingService.createBuilding(building);
        return new ResponseEntity<>(createdBuilding, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Building> getBuildingById(@PathVariable Long id) {
        Building fetchedBuilding = buildingService.getBuildingById(id);
        return ResponseEntity.ok(fetchedBuilding);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<Building> approveBuilding(@PathVariable Long id){
        Building approvedBuilding = buildingService.approveBuilding(id);
        return ResponseEntity.ok(approvedBuilding);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBuilding(@PathVariable Long id){
        buildingService.deleteBuilding(id);
        return ResponseEntity.ok(String.format("Buidling with id: %d was succesfully deleted", id));
    }

}
