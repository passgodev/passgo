package pl.uj.passgo.controllers;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.CacheControl;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = HealthcheckController.class)
@AutoConfigureMockMvc
public class HealthcheckControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHealthcheckEndpoint_returnsNoContent() throws Exception {
        mockMvc.perform(get("/health"))
               .andDo(print())
               .andExpect(status().isNoContent());
    }

    @Test
    public void testHealthcheckEndpoint_containsCacheControlNoStore() throws Exception {
        mockMvc.perform(get("/health"))
               .andDo(print())
               .andExpect(header().string("Cache-Control", CacheControl.noStore().getHeaderValue()));
    }
}
