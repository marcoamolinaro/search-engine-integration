package br.com.ovd.kafka.integration.stream.io;

import br.com.ovd.kafka.integration.model.ProductWithStocks;
import br.com.ovd.kafka.integration.model.source.Product;
import br.com.ovd.kafka.integration.model.source.StockList;
import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Data;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

@ApplicationScoped
@Data
public class CustomSerdes {
    Serde<String> string = Serdes.String();

    Serde<String> stringKeyProductSerde = Serdes.String();
    Serde<String> jsonKeyProductSerde = new Serdes.StringSerde();

    Serde<String> stringKeyStockListSerde = Serdes.String();
    Serde<String> jsonKeyStockListSerde = new Serdes.StringSerde();

    Serde<String> stringKeyProductWithStockSerde = Serdes.String();

    // Product
    Serde<Product> productSerde = new ObjectMapperSerde<>(Product.class);

    // StockList
    Serde<StockList> stockListSerde = new ObjectMapperSerde<>(StockList.class);

    Serde<ProductWithStocks> productWithStocksSerde = new ObjectMapperSerde<>(ProductWithStocks.class);
}
