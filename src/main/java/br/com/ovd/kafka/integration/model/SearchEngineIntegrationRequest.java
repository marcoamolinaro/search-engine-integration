package br.com.ovd.kafka.integration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private DetailsRequest details;
    private List<CategoryRequest> categories;
    private ImageRequest image;
}
