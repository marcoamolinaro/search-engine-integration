package br.com.ovd.kafka.integration.stream.transform;

import br.com.ovd.kafka.integration.client.SearchEngineIntegrationClient;
import br.com.ovd.kafka.integration.model.CategoryRequest;
import br.com.ovd.kafka.integration.model.ProductWithStocks;
import br.com.ovd.kafka.integration.model.SearchEngineIntegrationRequest;
import br.com.ovd.kafka.integration.model.source.*;
import br.com.ovd.kafka.integration.stream.config.IntegrationConfig;
import br.com.ovd.kafka.integration.stream.config.SiteConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
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
        String LOG_PATH = "[SearchEngineIntegrationProcessor->process]";
        logger.infof("-- INICIO %s -- [searchEngineUrl] - [%s]", LOG_PATH, integrationConfig.searchEngineUrl());

        Product product = value.getProduct();
        StockList stockList = value.getStocks();

        for (SiteConfig siteConfig: integrationConfig.sites()) {
            logger.infof("-- %s -- Site [%s]", LOG_PATH, siteConfig.site());

            List<ProductSite> sites = product.getSites();
            if (sites == null) {
                logger.info("-- getSites() returned null");
                continue;
            }

            Optional<ProductSite> productSiteOpt = sites.stream()
                    .filter(s -> s.getCodigoSite().equals(siteConfig.site()))
                    .findFirst();

            if (productSiteOpt.isEmpty()) {
                logger.infof("-- %s não será integrado com a filial pois não " +
                        "consta na lista de sites do produto", LOG_PATH, safeString(product.getModelo()));
                continue;
            }

            for (String sourceOrg : siteConfig.orgs()) {
                Optional<Stock> stockOpt = stockList.getEstoques().stream()
                        .filter(s -> s.getFilial().equals(sourceOrg))
                        .findFirst();

                if (stockOpt.isEmpty()) {
                    logger.infof("-- %s Produto [%s] não será integrado com a filial pois não consta na lista " +
                                    "de estoques do produto", LOG_PATH, safeString(product.getModelo()));
                    continue;
                }

                String productId = key;
                Stock stock = stockOpt.get();
                ProductSite productSite = productSiteOpt.get();

                SearchEngineIntegrationRequest request = setRequestAttributes(productId, productSite, stock, siteConfig, value);

                logger.infof("-- request [%s]", request.toString());

                SearchEngineIntegrationClient client = new SearchEngineIntegrationClient();
                client.setUrl(integrationConfig.searchEngineUrl());
                client.setEngineIntegrationRequest(request);

                logger.infof("-- client [%s]", client.toString());

                boolean changedProduct = product.isChanged();
                if (changedProduct) {
                    createProduct(productId, client);
                    return;
                }

                boolean changedStock = stockList.isChanged();
                if (changedStock) {
                    updateProduct(productId, client);
                }
            }
        }

        logger.infof("-- FIM %s -- [searchEngineUrl [%s]", LOG_PATH, integrationConfig.searchEngineUrl());
    }

    private SearchEngineIntegrationRequest setRequestAttributes(String productId, ProductSite productSite, Stock stock,
                                                                SiteConfig siteConfig, ProductWithStocks value) {
        SearchEngineIntegrationRequest request = new SearchEngineIntegrationRequest();

        Product product = value.getProduct();

        String status =
                (productSite.getStatus()
                        .trim()
                        .equalsIgnoreCase("inactive")
                        ? "removed" : stock.getStatus());

        request.setApiKey(siteConfig.key());
        request.setSecretKey(siteConfig.secret());
        request.setSalesChannel(stock.getFilial());
        request.setStatus(status);

        // preparar mensagem
        request.setName(product.getDescricao());
        request.setUrl("produtos/" + productId);
        if (!(safeString(product.getAplicacoes()).isEmpty()) && !(safeString(product.getDestaques()).isEmpty())) {
            request.setDescription(product.getAplicacoes() + " " + product.getDestaques());
        } else {
            request.setDescription("");
        }

        List<CategoryRequest> categoryRequestList = new ArrayList<>();
        if (product.getGrupo() != null) {
            categoryRequestList.add(prepareCategoryRequest(product.getGrupo(), product.getGrupo(), ""));
        }

        if (product.getSubgrupo() != null) {
            categoryRequestList.add(prepareCategoryRequest(product.getSubgrupo(), product.getSubgrupo(), product.getGrupo()));
        }

        if (product.getCategoria() != null) {
            categoryRequestList.add(prepareCategoryRequest(product.getCategoria(), product.getCategoria(), product.getSubgrupo()));
        }

        request.setCategories(categoryRequestList);

        request.setPrice(new BigDecimal("0.01"));
        request.setBrand(product.getMarca());

        Map<String, Object> mapDetails = new HashMap<>();

        putIfNotNull(mapDetails, "modelo", product.getModelo());
        putIfNotNull(mapDetails, "referenciaFabricante", product.getReferenciaFabricante());
        putIfNotNull(mapDetails, "codigoProduto", productSite.getCodigoProduto());
        putIfNotNull(mapDetails, "sobEncomenda", stock.getSobEncomenda());

        if (product.getFiltros() != null) {
            for (Filtro filter : product.getFiltros()) {
                putIfNotNull(mapDetails, filter.getFiltro(), filter.getValor());
            }
        }

        request.setDetails(mapDetails);

        Map<String, String> mapImage = new HashMap<>();
        mapImage.put("default", productId + "_principal.jpg");
        request.setImage(mapImage);

        return request;
    }

    private String safeString(String str) {
        return str != null ? str : "";
    }

    private void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private CategoryRequest prepareCategoryRequest(String id, String name, String parent) {
        CategoryRequest categoryRequest = new CategoryRequest();
        List<String> parents = new ArrayList<>();

        categoryRequest.setId(safeString(id));
        categoryRequest.setName(safeString(name));

        if (!safeString(parent).isEmpty()) {
            parents.add(parent);
        }

        categoryRequest.setParents(parents);

        return categoryRequest;
    }

    private void createProduct(String id, SearchEngineIntegrationClient client) {
        String LOG_PATH = "[SearchEngineIntegrationProcessor->createProduct] ";

        logger.infof("-- %s - Será executada integração completa --", LOG_PATH);
        logger.infof("JSON -> %s", client.getEngineIntegrationRequest().toJson());

        CompletionStage<String> result = client.alterar(id);
        result.thenAcceptAsync(logger::info);

        logger.infof("-- %s - Alteração retornou [%s] --", LOG_PATH, result.toString());
    }

    private void updateProduct(String id, SearchEngineIntegrationClient client) {
        String LOG_PATH = "[SearchEngineIntegrationProcessor->updateProduct]";

        logger.infof("-- %s - Será executado atualização do status do produto --", LOG_PATH);

        CompletionStage<String> result = client.salvar(id);
        result.thenAcceptAsync(logger::info);

        logger.infof("-- %s - Salvar retornou [%s] --", LOG_PATH, result.toString());
    }
}

