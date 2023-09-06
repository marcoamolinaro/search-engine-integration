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
public class Product {
    private String descricao;
    private String aplicacoes;
    private String destaques;
    private String grupo;
    private String subgrupo;
    private String categoria;
    private String marca;
    private String modelo;
    private String referenciaFabricante;
    private List<ProductSite> sites;
    private List<Attribute> filtros;
    private List<Attribute> atributos;
    private boolean isChanged = false;
}
