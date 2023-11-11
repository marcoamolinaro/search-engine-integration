package br.com.ovd.kafka.integration.stream.io;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;

@ApplicationScoped
public class Materializes {
    public <T> Materialized<String, T, KeyValueStore<Bytes, byte[]>> asStateStore(
            String storeName,
            Serde<String> keySerde,
            Serde<T> valueSerde
    ) {
        return Materialized.<String, T, KeyValueStore<Bytes, byte[]>>as(storeName)
                .withKeySerde(keySerde)
                .withValueSerde(valueSerde);
    }
}
