package br.com.ovd.kafka.integration.stream.transform;

import br.com.ovd.kafka.integration.model.source.Stock;
import br.com.ovd.kafka.integration.model.source.StockList;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.Optional;

@ApplicationScoped
public class StockListReducer {

    @Inject
    Logger logger;

    public StockList reduce(StockList old, StockList curr) {
        logger.infof("old.StockList [%s]", old.toString());
        logger.infof("curr.StockList [%s]", curr.toString());

        logger.info("-- INICIO [StockListReducer->reduce] -- ");

        if (curr.equals(new StockList())) {
            logger.info("-- FIM [StockListReducer->reduce] -- curr [null]");

            return curr;
        }

        if (old.getEstoques() != null && curr.getEstoques() != null) {
            for (Stock currStock: curr.getEstoques()) {
                String currFilial = currStock.getFilial();
                Optional<Stock> oldStockOpt = old.getEstoques().stream()
                        .filter(s -> s.getFilial().equals(currFilial))
                        .findFirst();

                if (oldStockOpt.isPresent()) {
                    Stock oldStock = oldStockOpt.get();
                    String oldStatus = oldStock.getStatus() != null ? oldStock.getStatus().trim() : "";
                    String currStatus = currStock.getStatus() != null ? currStock.getStatus().trim() : "";

                    if (!currStatus.equalsIgnoreCase(oldStatus)) {
                        currStock.setChanged(true);
                    }
                } else {
                    currStock.setChanged(true);
                }
            }

            for (Stock oldStock : old.getEstoques()) {
                String oldFilial = oldStock.getFilial();
                Optional<Stock> newStockOpt = curr.getEstoques().stream()
                        .filter(s -> s.getFilial().equals(oldFilial))
                        .findFirst();

                if (newStockOpt.isEmpty()) {
                    oldStock.setChanged(true);
                    logger.infof("caiu aqui! [%s]", oldStock);
                    curr.getEstoques().add(oldStock);
                }
            }

            for (Stock currStock : curr.getEstoques()) {
                if (currStock.isChanged()) {
                    curr.setChanged(true);
                }
             }
        }

        logger.info("-- FIM [StockListReducer->reduce] -- curr [" + curr.toString() + "]");

        return curr;
    }
}
