package br.com.ovd.kafka.integration.model.source;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Stock {
    private String filial;
    private BigDecimal estoque;
    private String status;
    private boolean isChanged = false;
}
