package com.lincon.OpenSearchpoc.dto;

import com.lincon.OpenSearchpoc.dto.amounts.CreditoAmount;
import org.opensearch.client.opensearch._types.aggregations.StringTermsBucket;

public class CreditoProcessor implements AggregationProcessor {

    @Override
    public void process(StringTermsBucket bucket, SaleResponse saleResponse) {
        CreditoAmount credito = new CreditoAmount();
        bucket.aggregations().forEach((field, aggregate) -> {
            String value = aggregate.sum().valueAsString();
            switch (field) {
                case "valor_bruto_total":
                    credito.setAmount(value);
                    break;
                case "desconto_total":
                    credito.setDescountAmount(value);
                    break;
                case "valor_liquido_total":
                    credito.setNetAmount(value);
                    break;
            }

        });
        saleResponse.setCredito(credito);
    }
}
