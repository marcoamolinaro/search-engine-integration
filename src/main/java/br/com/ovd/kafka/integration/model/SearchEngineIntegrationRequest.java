package br.com.ovd.kafka.integration.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchEngineIntegrationRequest {
    private String apiKey;
    private String secretKey;
    private String salesChannel;
    private String name;
    private String url;
    private String description;
    private String status;
    private BigDecimal price;
    private String brand;
    private Map<String, Object> details;
    private List<CategoryRequest> categories;
    private Map<String, String> image;

    public String toJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
