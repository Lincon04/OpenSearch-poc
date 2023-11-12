package com.lincon.OpenSearchpoc.controller;

import com.lincon.OpenSearchpoc.dto.Sale;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch.core.SearchResponse;
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

    @GetMapping("/get")
    public Sale get() throws IOException {
        SearchResponse<Sale> response = openSearchClient.search(s -> s.index("sales")
                .query(q -> q.match(m -> m.field("nsu").query(FieldValue.of(993484)))), Sale.class);

        System.out.println(response.hits().hits());

        List<Hit<Sale>> retorno = response.hits().hits();
        return retorno.get(0).source();
    }
}
