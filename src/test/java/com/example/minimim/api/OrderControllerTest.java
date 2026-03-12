package com.example.minimim.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOrderAndListOrders() throws Exception {
        String requestBody = """
                {
                  "customerId": "cust-1234",
                  "productCode": "gpu-rx-8800",
                  "quantity": 2,
                  "unitPrice": 499.99,
                  "orderStatus": "CREATED"
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.lineTotal").value(999.98));

        mockMvc.perform(get("/api/orders").param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productCode").value("gpu-rx-8800"));
    }

    @Test
    void createOrderAsync() throws Exception {
        String requestBody = """
                {
                  "customerId": "cust-5678",
                  "productCode": "ssd-2tb",
                  "quantity": 1,
                  "unitPrice": 149.95,
                  "orderStatus": "PROCESSING"
                }
                """;

        MvcResult mvcResult = mockMvc.perform(post("/api/orders/async")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.productCode").value("ssd-2tb"));
    }

    @Test
    void mockJobCreatesOrdersInBackground() throws Exception {
        String requestBody = """
                {
                  "orderCount": 12
                }
                """;

        MvcResult createJobResult = mockMvc.perform(post("/api/jobs/mock-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        JsonNode jobNode = objectMapper.readTree(createJobResult.getResponse().getContentAsString());
        String jobId = jobNode.get("id").asText();

        String state = null;
        for (int i = 0; i < 30; i++) {
            MvcResult pollResult = mockMvc.perform(get("/api/jobs/{jobId}", jobId))
                    .andExpect(status().isOk())
                    .andReturn();
            JsonNode polledNode = objectMapper.readTree(pollResult.getResponse().getContentAsString());
            state = polledNode.get("state").asText();
            if ("COMPLETED".equals(state)) {
                break;
            }
            Thread.sleep(100);
        }

        assertThat(state).isEqualTo("COMPLETED");
    }
}
