package br.com.ovd.kafka.integration.stream.transform;

import br.com.ovd.kafka.integration.model.source.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProductMapper {

    @Inject
    Logger logger;

    public Product map(Product source) {
        logger.info("-- INICIO [Product->map] --");

        if (source == null) {
            logger.info("-- FIM [Product->map] -- [curr new Product()]");
            return new Product();
        }

        logger.infof("-- FIM [Product->map] -- [%s]", source);
        return source;
    }
}
