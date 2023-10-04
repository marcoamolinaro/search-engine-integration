package br.com.ovd.kafka.integration.stream.transform;

import br.com.ovd.kafka.integration.model.source.Product;
import br.com.ovd.kafka.integration.model.source.Stock;
import br.com.ovd.kafka.integration.model.source.StockList;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class StockListReducer {
    public StockList reduce(StockList old, StockList curr) {
        if (curr == null) {
            curr = new StockList();
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

        return curr;
    }
}
