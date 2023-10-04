package br.com.ovd.kafka.integration.stream.transform;

import br.com.ovd.kafka.integration.model.ProductWithStocks;
import br.com.ovd.kafka.integration.model.source.Product;
import br.com.ovd.kafka.integration.model.source.StockList;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductWithStocksJoiner {
    public ProductWithStocks join(Product product, StockList stocks) {
        ProductWithStocks productWithStocks = new ProductWithStocks();

        productWithStocks.setProduct(product);
        productWithStocks.setStocks(stocks);

        return productWithStocks;
    }

}
