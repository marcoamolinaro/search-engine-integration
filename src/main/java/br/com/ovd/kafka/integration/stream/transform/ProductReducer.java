package br.com.ovd.kafka.integration.stream.transform;

import br.com.ovd.kafka.integration.model.source.Product;
import br.com.ovd.kafka.integration.model.source.ProductSite;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

public class ProductReducer {

    @Inject
    Logger logger;

    @ApplicationScoped
    public Product reduce(Product old, Product curr) {
        logger.info("-- INICIO [Product->reduce] --");

        if (curr.equals(new Product())) {
            logger.info("-- FIM [Product->reduce] -- [curr null]");

            return curr;
        }

        if (!old.equals(curr)) {
            curr.setChanged(true);
        }

        if (old.getSites() != null) {
            for (ProductSite pso : old.getSites()) {
                boolean isOldSitePresentInCurrSites = curr.getSites().stream()
                        .map(ProductSite::getCodigoSite)
                        .anyMatch(siteCode -> siteCode.equals(pso.getCodigoSite()));

                if (!isOldSitePresentInCurrSites) {
                    ProductSite newProductSite = new ProductSite();
                    newProductSite.setCodigoSite(pso.getCodigoSite());
                    newProductSite.setCodigoProduto(pso.getCodigoProduto());
                    newProductSite.setStatus("removed");

                    curr.getSites().add(newProductSite);
                }
            }
        }

        logger.info("-- FIM [Product->reduce] -- [curr " + curr.toString() +"]");

        return curr;
    }
}
