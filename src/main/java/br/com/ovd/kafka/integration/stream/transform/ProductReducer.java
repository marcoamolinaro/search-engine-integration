package br.com.ovd.kafka.integration.stream.transform;

import br.com.ovd.kafka.integration.model.source.Product;
import br.com.ovd.kafka.integration.model.source.ProductSite;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Objects;

public class ProductReducer {

    @ApplicationScoped
    public Product reduce(Product old, Product curr) {
        if (curr == null) {
            curr = new Product();
            return curr;
        }

        if (old.equals(curr)) {
            for (ProductSite pso : old.getSites()) {
                if (!curr.getSites().contains(pso.getCodigoSite())) {
                    ProductSite newProductSite = new ProductSite();
                    newProductSite.setCodigoSite(pso.getCodigoSite());
                    newProductSite.setCodigoProduto(pso.getCodigoProduto());
                    newProductSite.setStatus("removed");
                    List<ProductSite> list = curr.getSites();
                    list.add(newProductSite);
                    curr.setSites(list);
                }
            }
        } else {
            curr.setChanged(true);
        }

        return curr;
    }
}
