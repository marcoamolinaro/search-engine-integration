package br.com.ovd.kafka.integration.model.source;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSite {
    private String codigoProduto;
    private String codigoSite;
    private String status;
}
