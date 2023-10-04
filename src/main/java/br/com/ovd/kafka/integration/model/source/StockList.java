package br.com.ovd.kafka.integration.model.source;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class StockList {
    private List<Stock> estoques;
    private boolean isChanged = false;
}
