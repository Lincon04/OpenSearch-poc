package com.lincon.OpenSearchpoc.dto;

import lombok.Data;
import org.opensearch.client.opensearch._types.aggregations.Aggregate;

import java.util.ArrayList;
import java.util.List;

@Data
public class SaleV3Response {
    List<SaleDailyResponse> saleDailyResponses;

    public void handler(Aggregate aggregate) {
        if (aggregate.isDateHistogram()) {
            int size = aggregate.dateHistogram().buckets().array().size();
            saleDailyResponses = new ArrayList<>(size);
            aggregate.dateHistogram().buckets().array().forEach(longTermsBucket -> {
                String keyDate = longTermsBucket.keyAsString();
                String[] split = keyDate.split("T");
                String key = split[0];
                SaleDailyResponse saleDailyResponse = foundOrCreate(key);
                saleDailyResponse.setCount(longTermsBucket.docCount());
                saleDailyResponse.dailyAggregation(longTermsBucket);
            });
        }
    }


    public void add(Sale sale){
        SaleDailyResponse response = searchSale(sale.getDataVenda());
        if(response!= null){
            response.add(sale);
        }
    }

    public SaleDailyResponse foundOrCreate(String keyDate) {
        SaleDailyResponse response = SaleDailyResponse.create(keyDate);
        SaleDailyResponse saleDailyResponse = searchSale(keyDate);
        if (saleDailyResponse != null) return saleDailyResponse;
        saleDailyResponses.add(response);
        return response;
    }

    private SaleDailyResponse searchSale(String keyDate) {
        for (SaleDailyResponse saleDailyResponse : saleDailyResponses) {
            if (saleDailyResponse.getKeyDate().equals(keyDate)) {
                return saleDailyResponse;
            }
        }
        return null;
    }
}
