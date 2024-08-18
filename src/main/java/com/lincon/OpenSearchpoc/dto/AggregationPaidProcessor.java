package com.lincon.OpenSearchpoc.dto;

import org.opensearch.client.opensearch._types.aggregations.StringTermsBucket;

public class AggregationPaidProcessor implements AggregationProcessor {

    @Override
    public void process(StringTermsBucket bucket, SaleResponse saleResponse) {
        bucket.aggregations().forEach((field, aggregate) -> {
            String value = aggregate.sum().valueAsString();
            switch (field) {
                case "valor_bruto_total":
                    saleResponse.setPaidAmount(value);
                    break;
                case "desconto_total":
                    saleResponse.setPaidDescountAmount(value);
                    break;
                case "valor_liquido_total":
                    saleResponse.setPaidNetAmount(value);
                    break;
            }
        });
    }
}
