package br.com.ovd.kafka.integration.stream.transform;

import br.com.ovd.kafka.integration.model.ProductWithStocks;
import br.com.ovd.kafka.integration.model.source.Product;
import br.com.ovd.kafka.integration.model.source.StockList;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProductWithStocksJoiner {

    @Inject
    Logger logger;

    public ProductWithStocks join(Product product, StockList stocks) {

        logger.info("-- INICIO [ProductWithStocks->join] -- ");

        ProductWithStocks productWithStocks = new ProductWithStocks();

        productWithStocks.setProduct(product);
        productWithStocks.setStocks(stocks);

        logger.info("-- FIM [ProductWithStocks->join] -- productWithStocks [" + productWithStocks.toString() + "]");

        return productWithStocks;
    }

}
