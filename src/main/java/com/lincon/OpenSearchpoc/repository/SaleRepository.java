package com.lincon.OpenSearchpoc.repository;

import com.lincon.OpenSearchpoc.dto.Sale;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public class SaleRepository {

    @Autowired
    private OpenSearchClient openSearchClient;

    public void save(Sale sale) throws IOException {
        //Cria um index request do tipo Sale, a requisição por padrão usa o metodo PUT,então é necessário passar o id do documento
        IndexRequest<Sale> indexRequest = new IndexRequest.Builder<Sale>()
                .index("sales").id(sale.getNsu().toString()).document(sale).build();

        //A requisição irá criar um indice salesv1 caso ele não exista e atualizar ou criar o documento sale
        IndexResponse indexResponse = openSearchClient.index(indexRequest);
    }
}
