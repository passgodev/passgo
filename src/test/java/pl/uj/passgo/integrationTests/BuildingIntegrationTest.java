package pl.uj.passgo.integrationTests;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class BuildingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createBuilding_shouldReturnCreatedBuilding() throws Exception {
        String jsonRequest = """
            {
                "name": "Test Building",
                "address": {
                    "country": "Polska",
                    "city": "Warszawa",
                    "street": "Main St",
                    "postalCode": "00-001",
                    "buildingNumber": "10"
                },
                "sectors": []
            }
            """;

        mockMvc.perform(post("/buildings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Building"))
                .andExpect(jsonPath("$.address.city").value("Warszawa"));
    }

    @Test
    void createComplexBuilding_shouldReturnCreatedBuildingWithSectorsAndRows() throws Exception {
        String jsonRequest = """
            {
              "name": "Tauron Arena",
              "address": {
                "country": "Poland",
                "postalCode": "44-344",
                "buildingNumber": "1",
                "street": "Łojasiewicza",
                "city": "Kraków"
              },
              "sectors": [
                {
                  "name": "Tribune A",
                  "standingArea": false,
                  "rows": [
                    { "rowNumber": 1, "seatsCount": 15 },
                    { "rowNumber": 2, "seatsCount": 15 }
                  ]
                }
              ]
            }
            """;

        mockMvc.perform(post("/buildings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Tauron Arena"))
                .andExpect(jsonPath("$.address.city").value("Kraków"))
                .andExpect(jsonPath("$.address.postalCode").value("44-344"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }


    @Test
    void getBuildings_shouldReturnList() throws Exception {
        mockMvc.perform(get("/buildings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }


    @Test
    void deleteBuilding_shouldReturnNoContent() throws Exception {
        String jsonRequest = """
            {
              "name": "To Delete",
              "address": {
                "country": "Poland",
                "postalCode": "22-222",
                "buildingNumber": "2",
                "street": "Delete St",
                "city": "Delete City"
              },
              "sectors": []
            }
            """;

        String response = mockMvc.perform(post("/buildings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Number idNumber = JsonPath.read(response, "$.id");
        Long id = idNumber.longValue();

        mockMvc.perform(delete("/buildings/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/buildings/{id}", id))
                .andExpect(status().isNotFound());
    }
}
