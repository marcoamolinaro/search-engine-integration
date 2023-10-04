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
import org.jboss.logging.Logger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApplicationScoped
public class SearchEngineIntegrationProcessor {

    @Inject
    Logger logger;

    @Inject
    IntegrationConfig integrationConfig;

    @ApplicationScoped
    public void process(String key, ProductWithStocks value) {

        logger.info("searchEngineUrl = " + integrationConfig.searchEngineUrl());
        System.out.println("searchEngineUrl = " + integrationConfig.searchEngineUrl());

        for (SiteConfig siteConfig: integrationConfig.sites()) {
            logger.info("Site " + siteConfig.site());
            for (String org : siteConfig.orgs()) {
                logger.info("Org " + org);
            }
        }
    }
}

