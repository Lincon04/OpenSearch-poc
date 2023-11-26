package com.lincon.OpenSearchpoc.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.lincon.OpenSearchpoc.controller.filter.SaleFilter;
import com.lincon.OpenSearchpoc.dto.Sale;
import com.lincon.OpenSearchpoc.repository.SaleRepository;
import com.lincon.OpenSearchpoc.repository.SaleRepositoryNewSearch;
import lombok.AllArgsConstructor;
import org.opensearch.client.opensearch.core.BulkResponse;
import org.opensearch.client.opensearch.core.GetResponse;
import org.opensearch.client.opensearch.core.bulk.BulkResponseItem;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;


@Service
@AllArgsConstructor
public class SaleService {

    private ObjectMapper objectMapper;

    private SaleRepository saleRepository;

    private SaleRepositoryNewSearch saleRepositoryNewSearch;

    public List<Sale> loadSales() throws IOException {
        File file = new File("C:\\Dev\\projetos\\OpenSearch-poc\\src\\main\\resources\\static\\Sales.json");
        JavaType type = TypeFactory.defaultInstance().constructCollectionType(List.class, Sale.class);
        return objectMapper.readValue(file, type);
    }

    public String save(Sale sale) {
        try {
            saleRepository.save(sale);
        } catch (IOException e) {
            return "Falha ao criar documento";
        }
        return "Documento criado com sucesso!";
    }

    public String saveAll(List<Sale> sales) {
        try {
            BulkResponse result = saleRepository.saveAll(sales);
            if (result.errors()) {
                System.out.println("Bulk had errors");
                for (BulkResponseItem item : result.items()) {
                    if (item.error() != null) {
                        System.out.println(item.error().reason());
                    }
                }
                return "Error on save same sales";
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Save sales with success!";
    }

    public Sale findByIdUsingGetRequest(String id) {
        try {
            GetResponse<Sale> response = saleRepository.findByIdUsingGetRequest(id);
            return response.source();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Sale findByIdUsingSearch(String id)  {
        try {
            List<Hit<Sale>> retorno = saleRepository.findByIdUsingSearch(id);
            if(!retorno.isEmpty()){
                return retorno.get(0).source();
            }
            return new Sale();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Sale findByRangeSearch(SaleFilter saleFilter)  {
        try {
            List<Hit<Sale>> retorno = saleRepository.findByRangeDateSearch(saleFilter);
            if(!retorno.isEmpty()){
                return retorno.get(0).source();
            }
            return new Sale();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public String update(Sale sale) {
        try {
            saleRepository.update(sale, sale.getDataVenda()+sale.getNsu());
            return "Update with success";
        } catch (IOException e) {
            return "Update Fail";
        }
    }

    public String delete(Sale sale) {
        try {
            boolean isDeleted = saleRepository.delete(sale);
            if(isDeleted) {
                return "Deleted doc with success";
            }
        } catch (IOException e) {
            return "Error on process of delete";
        }
        return "Deleted Fail";
    }

    public String deleteAll(List<Sale> sales) {
        try {
            BulkResponse result = saleRepository.deleteAll(sales);
            if (result.errors()) {
                System.out.println("Bulk had errors");
                for (BulkResponseItem item : result.items()) {
                    if (item.error() != null) {
                        System.out.println(item.error().reason());
                    }
                }
                return "Error on delete same sales";
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Delete sales with success!";
    }

    public List<Sale> findAll() throws IOException {
        return saleRepository.findAll();
    }

    /*
        Busca usando o filter como base para montar o objeto Query de forma dinamica
     */

    public Sale findByRange(SaleFilter saleFilter)  {
        try {
            List<Hit<Sale>> retorno = saleRepositoryNewSearch.findByRange(saleFilter);
            if(!retorno.isEmpty()){
                return retorno.get(0).source();
            }
            return new Sale();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Sale findAll(SaleFilter saleFilter)  {
        try {
            List<Hit<Sale>> retorno = saleRepositoryNewSearch.findAll(saleFilter);
            if(!retorno.isEmpty()){
                return retorno.get(0).source();
            }
            return new Sale();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
