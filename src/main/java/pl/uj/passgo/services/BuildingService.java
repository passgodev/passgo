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
import pl.uj.passgo.models.responses.BuidlingResponse;
import pl.uj.passgo.models.responses.FullBuildingResponse;
import pl.uj.passgo.repos.BuildingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BuildingService {

    private final BuildingRepository buildingRepository;

    public List<BuidlingResponse> getAllBuildings(Boolean approved) {
        if(approved == null)
            return buildingRepository.findAll().stream().map(BuildingService::mapBuidlingToBuidlingResponse).toList();
        else
            return buildingRepository.findByApproved(approved).stream().map(BuildingService::mapBuidlingToBuidlingResponse).toList();
    }

    public BuidlingResponse createBuilding(BuildingRequest buildingRequest) {
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
                for (int i = 0; i < rowRequest.getSeatsCount(); i++) {
                    Seat seat = new Seat();
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
  
        return mapBuidlingToBuidlingResponse(buildingRepository.save(building));
    }

    public Building getBuildingById(Long id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("There is no building with id: %d", id)
                ));
    }

    public FullBuildingResponse getFullBuidlingById(Long id){
        return mapBuildingToFullBuildingResponse(getBuildingById(id));
    }

    public BuidlingResponse approveBuilding(Long id) {
        Building building = getBuildingById(id);
        building.setApproved(true);
        return mapBuidlingToBuidlingResponse(buildingRepository.save(building));
    }

    public void deleteBuilding(Long id) {
        Building building = getBuildingById(id);
        buildingRepository.deleteById(id);
    }

    private static BuidlingResponse mapBuidlingToBuidlingResponse(Building building){
        return new BuidlingResponse(building.getId(), building.getName(), building.getAddress(), building.getApproved());
    }

    private static FullBuildingResponse mapBuildingToFullBuildingResponse(Building building){
        return new FullBuildingResponse(
                building.getId(),
                building.getName(),
                building.getAddress(),
                building.getApproved(),
                building.getSectors()
        );
    }
}
