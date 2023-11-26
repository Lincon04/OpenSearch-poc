package com.lincon.OpenSearchpoc.repository;

import com.lincon.OpenSearchpoc.controller.filter.SaleFilter;
import com.lincon.OpenSearchpoc.dto.Sale;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
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

    public List<Hit<Sale>> findByRange(SaleFilter saleFilter) throws IOException {
        SaleQueryBuilder saleQueryBuilder = new SaleQueryBuilder(saleFilter);
        SearchRequest searchRequest = new SearchRequest.Builder().index(INDEX)
                .query(saleQueryBuilder.findByRange(saleFilter.getData_venda(), saleFilter.getData_recebimento())).build();
        SearchResponse<Sale> response = openSearchClient.search(searchRequest, Sale.class);
        return response.hits().hits();
    }

    public List<Hit<Sale>> findAll(SaleFilter saleFilter) throws IOException {
        SaleQueryBuilder saleQueryBuilder = new SaleQueryBuilder(saleFilter);
        SearchRequest searchRequest = new SearchRequest.Builder().index(INDEX)
                .query(saleQueryBuilder.findAll()).build();
        SearchResponse<Sale> response = openSearchClient.search(searchRequest, Sale.class);
        return response.hits().hits();
    }

}