package com.lincon.OpenSearchpoc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lincon.OpenSearchpoc.dto.Sale;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.BulkResponse;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.bulk.BulkResponseItem;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/OpenSearchController")
public class OpenSearchController {

    @Autowired
    private OpenSearchClient openSearchClient;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/get")
    public Sale get() throws IOException {
        SearchResponse<Sale> response = openSearchClient.search(s -> s.index("sales")
                .query(q -> q.match(m -> m.field("nsu").query(FieldValue.of(993484)))), Sale.class);

        System.out.println(response.hits().hits());

        List<Hit<Sale>> retorno = response.hits().hits();
        return retorno.get(0).source();
    }

    @GetMapping("/put")
    public Sale put() throws IOException {
        List<Sale> sales = new Sale().loadSales();
        BulkRequest.Builder br = new BulkRequest.Builder();

//        for (Sale sale: sales){
//            br.operations(op-> op.index(idx -> idx.index("sales").id(sale.getNsu().toString()).document(sale)));
//        }

        for (Sale sale: sales){
            br.operations(op-> op.delete(builder -> builder.index("sales").id(sale.getNsu().toString())));
        }

        BulkResponse result = openSearchClient.bulk(br.build());

        if (result.errors()) {
            System.out.println("Bulk had errors");
            for (BulkResponseItem item: result.items()) {
                if (item.error() != null) {
                    System.out.println(item.error().reason());
                }
            }
        }
        return sales.get(0);
    }
}
