package br.com.ovd.kafka.integration.stream.transform;

import br.com.ovd.kafka.integration.model.source.Stock;
import br.com.ovd.kafka.integration.model.source.StockList;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StockStatusMapper {
    public StockList map(StockList source) {
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

        return updated;
    }
}
