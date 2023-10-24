package br.com.ovd.kafka.integration.stream.transform;

import br.com.ovd.kafka.integration.model.source.Stock;
import br.com.ovd.kafka.integration.model.source.StockList;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class StockListReducer {

    @Inject
    Logger logger;

    public StockList reduce(StockList old, StockList curr) {

        logger.info("-- INICIO [StockListReducer->reduce] -- ");

        if (curr == null) {
            curr = new StockList();
            logger.info("-- FIM [StockListReducer->reduce] -- curr [null]");
            return curr;
        }

        for (Stock stock: curr.getEstoques()) {
            Stock oldStock = old.getEstoques().stream().findFirst().get();
            if (stock == null || !stock.getStatus().equals(oldStock.getStatus())) {
                stock.setChanged(true);
            }
        }

        for (Stock stock: old.getEstoques()) {
            Stock newStock = curr.getEstoques().stream().findFirst().get();
            if (!stock.getFilial().equals(newStock.getFilial())) {
                newStock.setFilial(stock.getFilial());
                newStock.setChanged(true);
            }
        }

        for (Stock stock : curr.getEstoques()) {
            if (curr.isChanged()) {
                stock.setChanged(true);
            }
         }

        logger.info("-- FIM [StockListReducer->reduce] -- curr [" + curr.toString() + "]");

        return curr;
    }
}
