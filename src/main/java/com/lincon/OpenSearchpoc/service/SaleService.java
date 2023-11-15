package com.lincon.OpenSearchpoc.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.lincon.OpenSearchpoc.dto.Sale;
import lombok.AllArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.Result;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.bulk.BulkResponseItem;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;


@Service
@AllArgsConstructor
public class SaleService {

    private OpenSearchClient openSearchClient;


    @Autowired
    private ObjectMapper objectMapper;

    public List<Sale> loadSales() throws IOException {
        File file = new File("C:\\Dev\\projetos\\OpenSearch-poc\\src\\main\\resources\\static\\Sales.json");
        JavaType type = TypeFactory.defaultInstance().constructCollectionType(List.class, Sale.class);
        return objectMapper.readValue(file, type);
    }

    //01 Metodos para criar um indice caso não exista, inserir novos documentos
    public IndexResponse save(Sale sale) throws IOException {
        //Cria um index request do tipo Sale, a requisição por padrão usa o metodo PUT,então é necessário passar o id do documento
        //a requisição irá criar um indice salesv1 caso ele não exita e atualizar ou criar o documento sale
        IndexRequest<Sale> indexRequest = new IndexRequest.Builder<Sale>().index("salesv1").id(sale.getNsu().toString()).document(sale).build();
        IndexResponse indexResponse = openSearchClient.index(indexRequest);
        if (indexResponse.result().equals(Result.NoOp)) {
            System.out.println("falha ao criar registro");
        }
        return openSearchClient.index(indexRequest);
    }

    public List<BulkResponseItem> saveAll(List<Sale> sales) throws IOException {
        BulkRequest.Builder br = new BulkRequest.Builder();

        for (Sale sale: sales){
            br.operations(op-> op.index(idx -> idx.index("sales").id(sale.getDataVenda()+sale.getNsu()).document(sale)));
        }

//        for (Sale sale: sales){
//            br.operations(op-> op.delete(builder -> builder.index("sales").id(sale.getNsu().toString())));
//        }

        BulkResponse result = openSearchClient.bulk(br.build());

        if (result.errors()) {
            System.out.println("Bulk had errors");
            for (BulkResponseItem item: result.items()) {
                if (item.error() != null) {
                    System.out.println(item.error().reason());
                }
            }
        }

        return result.items();
    }

    //02 Metodos para consultar um documneto, pelo id
    public Sale findById(String id, Sale sale) throws IOException {
        GetRequest request = new GetRequest.Builder().index("salesv1").id(sale.getNsu().toString()).build();
        GetResponse<Sale> response = openSearchClient.get(request, Sale.class);
        System.out.println(response);
        return response.source();
    }

    public Sale findById(String id) throws IOException {
        SearchResponse<Sale> response = openSearchClient.search(s -> s.index("sales")
                .query(q -> q.match(m -> m.field("nsu").query(FieldValue.of(993484)))), Sale.class);

        System.out.println(response.hits().hits());

        List<Hit<Sale>> retorno = response.hits().hits();
        return retorno.get(0).source();
    }

    //03 Metodos para atualizar um documento
    public Sale update(Sale sale, Sale sale1) throws IOException {
        UpdateRequest<Sale, Sale> updateRequest = new UpdateRequest.Builder<Sale, Sale>().index("salesv1").id(sale.getNsu().toString()).doc(sale1).build();
        UpdateResponse<Sale> updateResponse = openSearchClient.update(updateRequest, Sale.class);
        assert updateResponse.get() != null;
        return sale1;
    }

    //04 Metodos para deletar um documento
    public boolean delete(Sale sale) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest.Builder().index("salesv1").id(sale.getNsu().toString()).build();
        DeleteResponse deleteResponse = openSearchClient.delete(deleteRequest);
        return deleteResponse.result().equals(Result.Deleted);
    }
}
