package br.com.ovd.kafka.integration.stream.io;

import br.com.ovd.kafka.integration.model.source.Product;
import br.com.ovd.kafka.integration.model.source.StockList;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.kafka.streams.kstream.Consumed;
import org.jboss.logging.Logger;

@ApplicationScoped
public class Consumers {
    @Inject
    CustomSerdes serdes;

    @Inject
    Logger logger;

    public Consumed<String, Product> asProductConsumed() {
        logger.info("-- INICIO [Consumers->Consumed] --");

        Consumed<String, Product> productConsumed =
                Consumed.with(serdes.stringKeyPorductSerde, serdes.productSerde)
                .withName("product-consumer");

        logger.info("-- FIM [Consumers->Consumed] productConsumed [" + productConsumed.withKeySerde(serdes.stringKeyPorductSerde).toString() + "] --");

        return productConsumed;
    }

    public Consumed<String, StockList> asStockListConsumed() {
        return Consumed.with(serdes.stringKeyStockListSerde, serdes.stockLislSerde)
                .withName("stockList-consumer");
    }
}
