package br.com.ovd.kafka.integration.stream.transform;

import br.com.ovd.kafka.integration.model.source.Stock;
import br.com.ovd.kafka.integration.model.source.StockList;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
@ApplicationScoped
public class StockStatusMapper {

    @Inject
    Logger logger;

    public StockList map(StockList source) {

        logger.info("-- INICIO [StockStatusMapper->map] --");

        StockList updated = new StockList();

        for (Stock s : source.getEstoques()) {
            if (s.getStatus().equalsIgnoreCase("INATIVO")) {
                s.setStatus("removed");
            } else {
                s.setStatus("available");
            }

            if (s.getStatus().equalsIgnoreCase("available")
                    && s.getEstoque().intValue() == 0) {
                s.setStatus("unavailable");
            }
        }

        logger.info("-- FIM [StockStatusMapper->map] updated [" + updated.toString() + "--");

        return updated;
    }
}
