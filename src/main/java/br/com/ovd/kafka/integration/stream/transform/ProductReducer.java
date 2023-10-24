package br.com.ovd.kafka.integration.stream.transform;

import br.com.ovd.kafka.integration.model.source.Product;
import br.com.ovd.kafka.integration.model.source.ProductSite;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;

public class ProductReducer {

    @Inject
    Logger logger;

    @ApplicationScoped
    public Product reduce(Product old, Product curr) {

        logger.info("-- INICIO [Product->reduce] --");

        if (curr == null) {
            curr = new Product();

            logger.info("-- FIM [Product->reduce] -- [curr null]");

            return curr;
        }

        if (old.equals(curr)) {
            for (ProductSite pso : old.getSites()) {
                if (!curr.getSites().contains(pso.getCodigoSite())) {
                    ProductSite newProductSite = new ProductSite();
                    newProductSite.setCodigoSite(pso.getCodigoSite());
                    newProductSite.setCodigoProduto(pso.getCodigoProduto());
                    newProductSite.setStatus("removed");
                    List<ProductSite> list = curr.getSites();
                    list.add(newProductSite);
                    curr.setSites(list);
                }
            }
        } else {
            curr.setChanged(true);
        }

        logger.info("-- FIM [Product->reduce] -- [curr " + curr.toString() +"]");

        return curr;
    }
}
