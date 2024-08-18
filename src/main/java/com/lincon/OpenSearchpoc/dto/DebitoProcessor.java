package com.lincon.OpenSearchpoc.dto;

import com.lincon.OpenSearchpoc.dto.amounts.DebitoAmounts;
import org.opensearch.client.opensearch._types.aggregations.StringTermsBucket;

public class DebitoProcessor implements AggregationProcessor {

    @Override
    public void process(StringTermsBucket bucket, SaleResponse saleResponse) {
        DebitoAmounts debitoAmounts = new DebitoAmounts();
        bucket.aggregations().forEach((field, aggregate) -> {
            String value = aggregate.sum().valueAsString();
            switch (field) {
                case "valor_bruto_total":
                    debitoAmounts.setAmount(value);
                    break;
                case "desconto_total":
                    debitoAmounts.setDescountAmount(value);
                    break;
                case "valor_liquido_total":
                    debitoAmounts.setNetAmount(value);
                    break;
            }
        });
        saleResponse.setDebitoAmounts(debitoAmounts);
    }
}
