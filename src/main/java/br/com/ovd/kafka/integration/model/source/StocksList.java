package br.com.ovd.kafka.integration.model.source;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StocksList {
    private List<Stock> estoques;
    private boolean isChanged = false;
}
