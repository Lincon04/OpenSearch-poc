package com.lincon.OpenSearchpoc.repository;

import com.lincon.OpenSearchpoc.controller.filter.SaleFilter;
import com.lincon.OpenSearchpoc.dto.Sale;
import com.lincon.OpenSearchpoc.dto.SaleResponse;
import com.lincon.OpenSearchpoc.dto.SaleV3Response;
import com.lincon.OpenSearchpoc.reflection.SearchableQuery;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.aggregations.*;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

@Repository
public class SaleRepositoryNewSearch {

    @Autowired
    private OpenSearchClient openSearchClient;

    private static final String INDEX = "sales";

    public SaleV3Response findAgg(SaleFilter saleFilter) throws IOException {
        SearchableQuery<SaleFilter> searchable = new SearchableQuery<>(saleFilter);
        Query query = searchable.findAll();

        SearchRequest searchRequest = new SearchRequest.Builder().index(INDEX).query(query)
                .size(saleFilter.getSize()).from(saleFilter.getFrom())
                .aggregations(saleFilter.handlerAggregation())
                .sort(SortOptions.of(builder ->
                        builder.field(builder1 -> builder1.field("data_venda")
                                .order(SortOrder.Desc))))
                .build();

        SearchResponse<Sale> response = openSearchClient.search(searchRequest, Sale.class);
        SaleV3Response saleV3Response = new SaleV3Response();
        response.aggregations().forEach((s, aggregate) -> {
            if(s.equals("data_venda")){
                saleV3Response.handler(aggregate);
            }
            receiveAggregate(aggregate);
        });

        SaleResponse saleResponse = SaleResponse.fromSearchResponse(response, saleFilter.getGroupBy());

        if (!response.hits().hits().isEmpty()) {
            for (Hit<Sale> hits : response.hits().hits()) {
                assert hits.source() != null;
                saleV3Response.add(hits.source());
            }
        }

        return saleV3Response;
    }

    public void receiveAggregate(Aggregate aggregate) {
        if (aggregate.isLterms()) {
            Buckets<LongTermsBucket> buckets = aggregate.lterms().buckets();
            List<LongTermsBucket> array = buckets.array();
            receiveLongTermArray(array);
        }else if(aggregate.isSterms()){
            Buckets<StringTermsBucket> buckets = aggregate.sterms().buckets();
            List<StringTermsBucket> array = buckets.array();
            receiveStringTermsArray(array);
        }else if (aggregate.isSum()){
            System.out.println(aggregate.sum().valueAsString());
        }
    }

    public void receiveLongTermArray(List<LongTermsBucket> array){
        array.forEach(longTermsBucket -> {
            longTermsBucket.aggregations().forEach((s, aggregate) -> {
                System.out.println(s);
                receiveAggregate(aggregate);
            });
        });
    }

    public void receiveStringTermsArray(List<StringTermsBucket> array){
        array.forEach(longTermsBucket -> {
            longTermsBucket.aggregations().forEach((s, aggregate) -> {
                System.out.println(s);
                receiveAggregate(aggregate);
            });
        });
    }
}


