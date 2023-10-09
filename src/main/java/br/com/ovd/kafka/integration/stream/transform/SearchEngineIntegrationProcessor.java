package br.com.ovd.kafka.integration.stream.transform;

import br.com.ovd.kafka.integration.client.SearchEngineIntegrationClient;
import br.com.ovd.kafka.integration.model.CategoryRequest;
import br.com.ovd.kafka.integration.model.ProductWithStocks;
import br.com.ovd.kafka.integration.model.SearchEngineIntegrationRequest;
import br.com.ovd.kafka.integration.model.source.Product;
import br.com.ovd.kafka.integration.model.source.ProductSite;
import br.com.ovd.kafka.integration.model.source.Stock;
import br.com.ovd.kafka.integration.stream.config.IntegrationConfig;
import br.com.ovd.kafka.integration.stream.config.SiteConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletionStage;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApplicationScoped
public class SearchEngineIntegrationProcessor {

    private final int TIMEOUT = 3;

    private HttpRequest requestToSend;
    private HttpClient httpClient;

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
                    String status =
                            (productSite
                                    .getStatus()
                                    .equalsIgnoreCase("inactive ")
                                    ? "removed" : stock.getStatus());

                    SearchEngineIntegrationRequest request = new SearchEngineIntegrationRequest();
                    request.setApiKey(siteConfig.key());
                    request.setSecretKey(siteConfig.secret());
                    request.setSalesChannel(stock.getFilial());
                    request.setStatus(status);

                    SearchEngineIntegrationClient client = new SearchEngineIntegrationClient();

                    client.setUrl(integrationConfig.searchEngineUrl());
                    client.setEngineIntegrationRequest(request);

                    if (value.getProduct().isChanged()) {
                        // escrever no log mensagem recebida
                        logger.info(">> Será executada integração completa <<");

                        // preparar mensagem
                        request.setName(value.getProduct().getDescricao());
                        request.setUrl("produtos/" + product_id);
                        request.setDescription(
                                value.getProduct().getAplicacoes() + " " +
                                        value.getProduct().getDestaques());

                        List<CategoryRequest> categoryRequestList = new ArrayList<>();

                        categoryRequestList.add(prepareCategoryRequest(value, false));
                        categoryRequestList.add(prepareCategoryRequest(value, true));
                        categoryRequestList.add(prepareCategoryRequest(value, true));

                        request.setPrice(new BigDecimal("0.01"));
                        request.setBrand(value.getProduct().getMarca());

                        // Mapenado details

                        Map<String, Object> mapDetails = new HashMap<>();

                        mapDetails.put("modelo", value.getProduct().getModelo());
                        mapDetails.put("referenciaFabricante", value.getProduct().getReferenciaFabricante());
                        mapDetails.put("codigoProduto", productSite.getCodigoProduto());
                        mapDetails.put("sobEncomenda", stock.getSobEncomenda());

                        request.setDetails(mapDetails);

                        // Mapeando categories
                        request.setCategories(categoryRequestList);

                        // Mapeando imagens
                        Map<String, String> mapImage = new HashMap<>();
                        mapImage.put("default", key + "_principal.jpg");

                        request.setImage(mapImage);

                        // Enviar PUT
                        CompletionStage<Long> result = client.alterar(product_id);

                        logger.info(">> Alteração retornou " + result.toString());

                    } else {
                        // TODO - POST
                        // escrever no log mensagem recebida
                        logger.info(">> Será executado atualização do status do produto <<");

                        // Enviar
                        CompletionStage<Long> result = client.salvar(product_id);

                        logger.info(">> Salvar retornou " + result.toString());
                    }
                }
            }
        }
    }

    private CategoryRequest prepareCategoryRequest(ProductWithStocks value, Boolean hasSubGroup) {
        CategoryRequest categoryRequest = new CategoryRequest();
        List<String> parents = new ArrayList<>();

        categoryRequest.setId(value.getProduct().getGrupo());
        categoryRequest.setName(value.getProduct().getGrupo());
        parents.add((hasSubGroup ? value.getProduct().getSubgrupo() : ""));
        categoryRequest.setParents(parents);

        return categoryRequest;
    }


}

