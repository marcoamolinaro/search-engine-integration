package br.com.ovd.kafka.integration.stream.transform;

import br.com.ovd.kafka.integration.model.ProductWithStocks;
import br.com.ovd.kafka.integration.stream.config.IntegrationConfig;
import br.com.ovd.kafka.integration.stream.config.SiteConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApplicationScoped
public class SearchEngineIntegrationProcessor {

    @Inject
    Logger logger;

    @Inject
    IntegrationConfig config;

    public void process(String key, ProductWithStocks value) {

    }
}
