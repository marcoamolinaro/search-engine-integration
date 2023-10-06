package br.com.ovd.kafka.integration.stream.io;

import br.com.ovd.kafka.integration.model.source.Product;
import br.com.ovd.kafka.integration.model.source.StockList;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.kafka.streams.kstream.Consumed;

@ApplicationScoped
public class Consumers {
    @Inject
    CustomSerdes serdes;

    public Consumed<String, Product> asProductConsumed() {
        return Consumed.with(serdes.stringKeyPorductSerde, serdes.productSerde)
                .withName("product-consumer");
    }

    public Consumed<String, StockList> asStockListConsumed() {
        return Consumed.with(serdes.stringKeyStockListSerde, serdes.stockLislSerde)
                .withName("stockList-consumer");
    }
}
