package br.com.ovd.kafka.integration.stream;

import br.com.ovd.kafka.integration.model.ProductWithStocks;
import br.com.ovd.kafka.integration.model.source.Product;
import br.com.ovd.kafka.integration.model.source.StockList;
import br.com.ovd.kafka.integration.stream.io.Consumers;
import br.com.ovd.kafka.integration.stream.io.CustomSerdes;
import br.com.ovd.kafka.integration.stream.io.Materializes;
import br.com.ovd.kafka.integration.stream.transform.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
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
    ProductMapper productMapper;

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

    @Inject
    CustomSerdes serdes;

    @Inject
    Materializes materialize;

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        logger.info("-- INICIO [TopologyProducer] --");

        KStream<String, Product> productStream = builder
                .stream(productSourceTopic, this.consumers.asProductConsumed())
                .peek((k,v) -> logger.infof("IN >> Produto. Key: %s, value: %s", k, v))
                .map((k, v) -> KeyValue.pair(k, productMapper.map(v)))
                .groupByKey(Grouped.with(serdes.getStringKeyProductSerde(), serdes.getProductSerde()))
                .reduce((prevProduct, currProduct) -> productReducer.reduce(prevProduct, currProduct),
                        materialize.asStateStore("product-compared", serdes.getString(), serdes.getProductSerde())
                )
                .toStream();

        logger.info("-- INICIO [TopologyProducer] productStream [" + productStream + "]");

        KTable<String, Product> filteredProductTable = productStream
                .filter((k,v) -> v.isChanged())
                .peek((k,v) -> logger.info("OUT >> v.isChanged() [" + v.isChanged() + "]"))
                .toTable(materialize.asStateStore("product-filtered", serdes.getString(), serdes.getProductSerde()));


        KStream<String, StockList> stocksListStream = builder
                .stream(stocksSourceTopic, this.consumers.asStockListConsumed())
                .peek((k,v) -> logger.infof("IN >> StockList. Key %s, Value %s", k, v))
                .map((k,v) -> KeyValue.pair(k, stockStatusMapper.map(v)))
                .groupByKey(Grouped.with(serdes.getStringKeyStockListSerde(), serdes.getStockListSerde()))
                .reduce((prevStock, currStock) -> stockListReducer.reduce(prevStock, currStock),
                        materialize.asStateStore("stocks-compared", serdes.getString(), serdes.getStockListSerde())
                )
                .toStream();

        KTable<String, StockList> filteredStocksTable = stocksListStream
                .peek((k,v) -> logger.infof("IN [reduced & map] >> StockList. Key %s, Value %s", k, v))
                .filter((k,v) -> v.isChanged())
                .peek((k,v) -> logger.infof("OUT [reduced & map] >> StockList. Key %s, Value %s", k, v))
                .toTable(materialize.asStateStore("stocks-filtered", serdes.getString(), serdes.getStockListSerde()));

        ValueJoiner<Product, StockList, ProductWithStocks> valueJoiner = (left, right) -> {
            return productWithStocksJoiner.join(left, right);
        };

        KTable<String, ProductWithStocks> productWithStocks = filteredProductTable
                .join(filteredStocksTable, valueJoiner)
                .toStream()
                .peek((k,v) -> logger.infof("OUT [productWithStocks] >> Join <PRODUCT, STOCKSLIST>. Key %s, Value %s", k, v))
                .toTable(materialize.asStateStore("product-with-stocks-joined", serdes.getString(), serdes.getProductWithStocksSerde()));

        productWithStocks.toStream().foreach((key, value) -> integrationProcessor.process(key, value));

        logger.info("-- FIM [TopologyProducer] --");

        return builder.build();
    }
}

