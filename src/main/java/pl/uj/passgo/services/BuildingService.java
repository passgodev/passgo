package pl.uj.passgo.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.Address;
import pl.uj.passgo.models.Building;
import pl.uj.passgo.models.DTOs.AddressRequest;
import pl.uj.passgo.models.DTOs.BuildingRequest;
import pl.uj.passgo.models.Event;
import pl.uj.passgo.repos.BuildingRepository;
import pl.uj.passgo.repos.EventRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BuildingService {

    private final BuildingRepository buildingRepository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    public List<Building> getAllBuildings() {
        return buildingRepository.findAll();
    }

    public Building createBuilding(BuildingRequest building) {
        AddressRequest addressRequest = building.getAddress();
        Address address = Address.builder()
                .country(addressRequest.getCountry())
                .city(addressRequest.getCity())
                .street(addressRequest.getStreet())
                .postalCode(addressRequest.getPostalCode())
                .buildingNumber(addressRequest.getBuildingNumber())
                .build();

        Building builtBuilding = Building.builder()
                .name(building.getName())
                .address(address)
                .build();

        return buildingRepository.save(builtBuilding);
    }

    public Building getBuildingById(Long id) {
        Optional<Building> buildingOptional = buildingRepository.findById(id);

        if(buildingOptional.isPresent()){
            return buildingOptional.get();
        }
        else{
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("There is no building with id: %d", id)
            );
        }
    }

}
