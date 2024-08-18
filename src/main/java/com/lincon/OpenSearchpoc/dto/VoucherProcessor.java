package com.lincon.OpenSearchpoc.dto;

import com.lincon.OpenSearchpoc.dto.amounts.VoucherAmount;
import org.opensearch.client.opensearch._types.aggregations.StringTermsBucket;

public class VoucherProcessor implements AggregationProcessor {

    @Override
    public void process(StringTermsBucket bucket, SaleResponse saleResponse) {
        VoucherAmount voucherAmount = new VoucherAmount();
        bucket.aggregations().forEach((field, aggregate) -> {
            String value = aggregate.sum().valueAsString();
            if(field.equals("valor_bruto_total")){
                voucherAmount.setMount(value);
            }
        });
        saleResponse.setVoucherAmount(voucherAmount);
    }
}
