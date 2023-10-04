package br.com.ovd.kafka.integration.stream;

import br.com.ovd.kafka.integration.model.ProductWithStocks;
import br.com.ovd.kafka.integration.model.source.Product;
import br.com.ovd.kafka.integration.model.source.StockList;
import br.com.ovd.kafka.integration.stream.io.Consumers;
import br.com.ovd.kafka.integration.stream.transform.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TopologyProducer {
    @ConfigProperty(name = "topic.source.product")
    String productSourceTopic;

    @ConfigProperty(name = "topic.source.stocks")
    String stocksSourceTopic;

    @Inject
    Logger logger;

    @Inject
    ProductReducer productReducer;

    @Inject
    StockStatusMapper stockStatusMapper;

    @Inject
    StockListReducer stockListReducer;

    @Inject
    ProductWithStocksJoiner productWithStocksJoiner;

    @Inject
    SearchEngineIntegrationProcessor integrationProcessor;

    @Inject
    Consumers consumers;

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        logger.info("-- INICIO --");

        KStream<String, Product> productStream = builder
                .stream(productSourceTopic, this.consumers.asProductConsumed())
                .peek((k,v) -> logger.infof("IN >> Produto. Key: %s, value: %s", k, v))
                .groupByKey()
                .reduce((k,v) -> productReducer.reduce(v,v),
                        Materialized.as("product-compared")).toStream();

        KTable<String, Product> productKTable =
                productStream
                        .filter((k, v) -> v.isChanged())
                        .toTable(Materialized.as("product-filtered"));

        KStream<String, StockList> stocksListKstream = builder
                .stream(stocksSourceTopic, this.consumers.asStockListConsumed())
                .peek((k,v) -> logger.infof("IN >> StockList. Key %, Value %", k, v))
                .mapValues((k,v) -> stockStatusMapper.map(v))
                .groupByKey()
                .reduce((k,v) -> stockListReducer.reduce(v,v),
                        Materialized.as("stocks-compared")).toStream();

        KTable<String, StockList> stringStocksListKTable =
                stocksListKstream
                        .filter((k,v) -> v.isChanged())
                        .toTable(Materialized.as("stocks-filtered"));

        // Join
        KTable<String, ProductWithStocks> productWithStocks =
                productKTable.join(stringStocksListKTable, (product, stocksList) ->
        productWithStocksJoiner.join(product, stocksList)).toStream()
                        .toTable(Materialized.as("product-with-stocks-joined"));

        KStream<String, ProductWithStocks> productWithStocksKStream = productWithStocks.toStream();

        productWithStocksKStream.foreach((key, value) -> integrationProcessor.process(key, value));

        return builder.build();
    }
}

