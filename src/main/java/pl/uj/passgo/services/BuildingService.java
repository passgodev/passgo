package pl.uj.passgo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.*;
import pl.uj.passgo.models.DTOs.AddressRequest;
import pl.uj.passgo.models.DTOs.buildingRequests.BuildingRequest;
import pl.uj.passgo.models.DTOs.buildingRequests.RowRequest;
import pl.uj.passgo.models.DTOs.buildingRequests.SectorRequest;
import pl.uj.passgo.models.responses.building.*;
import pl.uj.passgo.repos.BuildingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BuildingService {

    private final BuildingRepository buildingRepository;

    public List<BuildingResponse> getAllBuildings(BuildingStatus status) {
        if(status == null)
            return buildingRepository.findAll().stream().map(BuildingService::mapBuildingToBuildingResponse).toList();
        else
            return buildingRepository.findByStatus(status).stream().map(BuildingService::mapBuildingToBuildingResponse).toList();
    }

    public BuildingResponse createBuilding(BuildingRequest buildingRequest) {
        AddressRequest addressRequest = buildingRequest.getAddress();
        Address address = Address.builder()
                .country(addressRequest.getCountry())
                .city(addressRequest.getCity())
                .street(addressRequest.getStreet())
                .postalCode(addressRequest.getPostalCode())
                .buildingNumber(addressRequest.getBuildingNumber())
                .build();

        Building building = new Building();
        building.setAddress(address);
        building.setName(buildingRequest.getName());


        List<Sector> sectors = new ArrayList<>();
        for(SectorRequest sectorRequest : buildingRequest.getSectors()){
            Sector sector = new Sector();
            sector.setName(sectorRequest.getName());
            sector.setBuilding(building);
            sector.setStandingArea(sectorRequest.getStandingArea());

            List<Row> rows = new ArrayList<>();
            for (RowRequest rowRequest : sectorRequest.getRows()) {
                Row row = new Row();
                row.setRowNumber(rowRequest.getRowNumber());
                row.setSeatsCount(rowRequest.getSeatsCount());
                row.setSector(sector);

                List<Seat> seats = new ArrayList<>();
                for (long i = 1; i <= rowRequest.getSeatsCount(); i++) {
                    Seat seat = new Seat();
                    seat.setSeatNumber(i);
                    seat.setRow(row);
                    seats.add(seat);
                }

                row.setSeats(seats);
                rows.add(row);
            }
            sector.setRows(rows);
            sectors.add(sector);
        }

        building.setSectors(sectors);
  
        return mapBuildingToBuildingResponse(buildingRepository.save(building));
    }

    public Building getBuildingById(Long id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("There is no building with id: %d", id)
                ));
    }

    public FullBuildingResponse getFullBuildingById(Long id){
        return mapBuildingToFullBuildingResponse(getBuildingById(id));
    }

    public BuildingResponse updateBuildingStatus(Long id, BuildingStatus status) {
        Building building = getBuildingById(id);
        building.setStatus(status);
        return mapBuildingToBuildingResponse(buildingRepository.save(building));
    }

    public void deleteBuilding(Long id) {
        buildingRepository.deleteById(id);
    }

    private static BuildingResponse mapBuildingToBuildingResponse(Building building){
        return new BuildingResponse(building.getId(), building.getName(), mapAddressToAddressResponse(building.getAddress()), building.getStatus());
    }

    private static AddressResponse mapAddressToAddressResponse(Address address) {
        return new AddressResponse(
                address.getCountry(),
                address.getCity(),
                address.getStreet(),
                address.getPostalCode(),
                address.getBuildingNumber()
        );
    }

    private static FullBuildingResponse mapBuildingToFullBuildingResponse(Building building){
        return new FullBuildingResponse(
                building.getId(),
                building.getName(),
                building.getAddress(),
                building.getStatus(),
                building.getSectors().stream().map(BuildingService::mapSectorToSectorResponse).toList()
        );
    }

    private static SectorResponse mapSectorToSectorResponse(Sector sector) {
        return new SectorResponse(
                sector.getName(),
                sector.getStandingArea(),
                sector.getRows().stream().map(BuildingService::mapRowToRowResponse).toList()
        );
    }

    private static RowResponse mapRowToRowResponse(Row row) {
        return new RowResponse(
                row.getRowNumber(),
                row.getSeatsCount()
        );
    }
}
