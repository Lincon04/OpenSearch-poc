package com.lincon.OpenSearchpoc.repository;

import com.lincon.OpenSearchpoc.controller.filter.SaleFilter;
import com.lincon.OpenSearchpoc.dto.Sale;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.Result;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SaleRepository {

    @Autowired
    private OpenSearchClient openSearchClient;

    private static final String INDEX = "sales";

    public void save(Sale sale) throws IOException {
        //Cria um index request do tipo Sale, a requisição por padrão usa o metodo PUT,então é necessário passar o id do documento
        IndexRequest<Sale> indexRequest = new IndexRequest.Builder<Sale>()
                .index(INDEX).id(sale.getNsu().toString()).document(sale).build();

        //A requisição irá criar um indice salesv1 caso ele não exista e atualizar ou criar o documento sale
        openSearchClient.index(indexRequest);
    }

    public BulkResponse saveAll(List<Sale> sales) throws Exception {
        BulkRequest.Builder br = new BulkRequest.Builder();
        for (Sale sale : sales) {
            br.operations(op -> op.index(idx -> idx.index(INDEX)
                    .id(sale.getDataVenda() + sale.getNsu()).document(sale)));
        }
        return openSearchClient.bulk(br.build());
    }

    public GetResponse<Sale> findByIdUsingGetRequest(String id) throws IOException {
        GetRequest request = new GetRequest.Builder().index(INDEX).id(id).build();
        return openSearchClient.get(request, Sale.class);
    }

    public void update(Sale sale, String id) throws IOException {
        UpdateRequest<Sale, Sale> updateRequest = new UpdateRequest.Builder<Sale, Sale>()
                .index(INDEX).id(id).doc(sale).build();
        openSearchClient.update(updateRequest, Sale.class);
    }

    public boolean delete(Sale sale) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest.Builder()
                .index(INDEX).id(sale.getNsu().toString()).build();
        DeleteResponse deleteResponse = openSearchClient.delete(deleteRequest);
        return deleteResponse.result().equals(Result.Deleted);
    }

    public BulkResponse deleteAll(List<Sale> sales) throws IOException {
        BulkRequest.Builder br = new BulkRequest.Builder();

        for (Sale sale : sales) {
            br.operations(op -> op.delete(builder -> builder
                    .index(INDEX).id(sale.getNsu().toString())));
        }

        return openSearchClient.bulk(br.build());
    }

    public List<Sale> findAll() throws IOException {
        SearchRequest searchRequest = SearchRequest
                .of(builder -> builder
                        .index(INDEX)
                        .query(q -> q
                                .matchAll(m -> m
                                        .queryName("_search")
                                )
                        ).size(100)
                );
        SearchResponse<Sale> searchResponse = openSearchClient.search(searchRequest, Sale.class);
        List<Sale> sales = new ArrayList<>();
        for (Hit<Sale> hisSale : searchResponse.hits().hits()) {
            sales.add(hisSale.source());
        }
        return sales;
    }

    public List<Hit<Sale>> findByIdUsingSearch(String id) throws IOException {
        SearchResponse<Sale> response = openSearchClient.search(s -> s.index(INDEX)
                .query(q -> q.
                        match(m -> m.
                                field("nsu")
                                .query(FieldValue.of(id)
                                )
                        )
                ), Sale.class);
        return response.hits().hits();
    }

    public List<Hit<Sale>> findAllBy(SaleFilter saleFilter) throws IOException {
        JsonData saledate= JsonData.of(saleFilter.getStartDataVenda());
        JsonData paymentdate= JsonData.of(saleFilter.getDataRecebimento());
        SearchRequest searchRequest = new SearchRequest.Builder().index("sales")
                .query(q -> q.
                        bool(b -> b
                                .must(m -> m
                                        .match(f -> f
                                                .field("pv")
                                                .query(FieldValue.of(saleFilter.getPv()))
                                        )
                                ).must(m -> m
                                        .match(f -> f
                                                .field("nsu")
                                                .query(FieldValue.of(saleFilter.getNsu()))
                                        )
                                ).must(m -> m.range(f -> f.field("data_venda").gte(saledate)))
                                .must(m -> m.range(f -> f.field("data_recebimento").gte(paymentdate)))
                        )
                ).build();
        SearchResponse<Sale> response = openSearchClient.search(searchRequest, Sale.class);
        return response.hits().hits();
    }

    public List<Hit<Sale>> findByRangeDateSearch(SaleFilter saleFilter) throws IOException {
        JsonData saledate= JsonData.of(saleFilter.getStartDataVenda());
        JsonData paymentdate= JsonData.of(saleFilter.getDataRecebimento());
        SearchRequest searchRequest = new SearchRequest.Builder().index(INDEX)
                .query(query -> query
                        .range(range-> range
                                .field("data_venda")
                                .gte(saledate)
                                .lte(paymentdate)
                                .format("yyyy-MM-dd")
                        )
                ).build();
        SearchResponse<Sale> response = openSearchClient.search(searchRequest, Sale.class);
        return response.hits().hits();
    }

}
