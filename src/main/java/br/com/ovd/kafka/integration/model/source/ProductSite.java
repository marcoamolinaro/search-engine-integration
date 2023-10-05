package br.com.ovd.kafka.integration.model.source;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ProductSite {
    private String codigoProduto;
    private String codigoSite;
    private String status;
}
