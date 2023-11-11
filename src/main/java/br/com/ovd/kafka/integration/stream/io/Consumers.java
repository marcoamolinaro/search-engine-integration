package br.com.ovd.kafka.integration.stream.io;

import br.com.ovd.kafka.integration.model.source.Product;
import br.com.ovd.kafka.integration.model.source.StockList;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Data;
import org.apache.kafka.streams.kstream.Consumed;
import org.jboss.logging.Logger;

@Data
@ApplicationScoped
public class Consumers {
    @Inject
    CustomSerdes serdes;

    @Inject
    Logger logger;

    public Consumed<String, Product> asProductConsumed() {
        logger.info("-- INICIO [Consumers->Consumed] --");

        Consumed<String, Product> productConsumed =
                Consumed.with(serdes.getStringKeyProductSerde(), serdes.getProductSerde())
                .withName("product-consumer");

        logger.info("-- FIM [Consumers->Consumed] productConsumed [" + productConsumed.withKeySerde(serdes.stringKeyProductSerde).toString() + "] --");

        return productConsumed;
    }

    public Consumed<String, StockList> asStockListConsumed() {
        Consumed<String, StockList> stockListConsumed =
                Consumed.with(serdes.getStringKeyStockListSerde(), serdes.getStockListSerde())
                    .withName("stockList-consumer");

        logger.info("-- FIM [Consumers->Consumed] stockListConsumed [" + stockListConsumed.withKeySerde(serdes.stringKeyStockListSerde).toString() + "] --");

        return stockListConsumed;
    }
}
