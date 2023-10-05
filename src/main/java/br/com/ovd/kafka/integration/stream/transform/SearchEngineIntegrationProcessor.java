package br.com.ovd.kafka.integration.stream.transform;

import br.com.ovd.kafka.integration.model.ProductWithStocks;
import br.com.ovd.kafka.integration.model.source.Product;
import br.com.ovd.kafka.integration.model.source.ProductSite;
import br.com.ovd.kafka.integration.model.source.Stock;
import br.com.ovd.kafka.integration.model.source.StockList;
import br.com.ovd.kafka.integration.stream.config.IntegrationConfig;
import br.com.ovd.kafka.integration.stream.config.SiteConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.jboss.logging.Logger;

import java.util.Optional;

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

        for (SiteConfig siteConfig: integrationConfig.sites()) {
            logger.info("Site " + siteConfig.site());
            for (ProductSite productSite : value.getProduct().getSites()) {
                Product product = value.getProduct();

                Stock stock = null;

                for (String sourceOrg : siteConfig.orgs()) {
                    stock = value.getStocks()
                            .getEstoques()
                            .stream()
                            .filter(s -> s.getFilial().equals(sourceOrg))
                            .findFirst().get();
                }
                if (stock == null) {
                    logger.info("Produto " + product.getModelo()
                            + " não será integrado com a filial pois não consta na list de produtos.");
                } else {

                    String product_id = key;
                    String apiKey = siteConfig.key();
                    String secretKey = siteConfig.secret();
                    String salesChannel = stock.getFilial();
                    String status =
                            (productSite
                                    .getStatus()
                                    .equalsIgnoreCase("inactive ")
                                    ? "removed" : stock.getStatus());
                    if (value.getProduct().isChanged()) {
                        // TODO - PUT
                        // preparar mensagem
                        // enviar
                        // escrever no log mensagem recebida
                    } else {
                        // TODO - POST
                        // Enviar parametros gerais
                        // escrever no log mensagem recebida
                    }
                }
            }
        }
    }
}

