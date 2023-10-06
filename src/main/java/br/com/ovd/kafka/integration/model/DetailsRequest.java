package br.com.ovd.kafka.integration.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailsRequest {
    private String modelo;
    private String referenciaFabricante;
    private String codigoProduto;
    private Boolean sobEncomenda;
    private String Potência;
    @SerializedName("Rotações por minuto (rpm)")
    private String rpm;
    @SerializedName("Capacidade do mandril")
    private String capacidade;
}
