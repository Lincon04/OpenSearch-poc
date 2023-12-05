package com.lincon.OpenSearchpoc.repository;

import com.lincon.OpenSearchpoc.controller.filter.SaleFilter;
import com.lincon.OpenSearchpoc.dto.Sale;
import com.lincon.OpenSearchpoc.reflection.SearchableQuery;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class SaleRepositoryNewSearch {

    @Autowired
    private OpenSearchClient openSearchClient;

    private static final String INDEX = "sales";


    public List<Hit<Sale>> findAll(SaleFilter saleFilter) throws IOException {
        SearchableQuery<SaleFilter> searchable = new SearchableQuery<>(saleFilter);
        Query query = searchable.findAll();
        SearchRequest searchRequest = new SearchRequest.Builder().index(INDEX).query(query).size(100).build();
        SearchResponse<Sale> response = openSearchClient.search(searchRequest, Sale.class);
        return response.hits().hits();
    }

}
