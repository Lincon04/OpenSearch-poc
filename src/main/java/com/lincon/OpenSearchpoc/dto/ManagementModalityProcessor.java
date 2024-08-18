package com.lincon.OpenSearchpoc.dto;

import org.opensearch.client.opensearch._types.aggregations.Aggregate;
import org.opensearch.client.opensearch._types.aggregations.StringTermsBucket;

import java.util.List;

public class ManagementModalityProcessor implements ManagementProcessor {

    @Override
    public void management(Aggregate aggregate, SaleResponse saleResponse) {
        List<StringTermsBucket> innerBuckets = aggregate.sterms().buckets().array();
        processInnerBuckets(innerBuckets, saleResponse);
    }

    @Override
    public void processInnerBuckets(List<StringTermsBucket> buckets, SaleResponse saleResponse) {
        buckets.forEach(bucket -> {
            AggregationProcessor processor = getProcessor(bucket.key());
            if (processor != null) {
                processor.process(bucket, saleResponse);
            }
        });
    }

    public AggregationProcessor getProcessor(String key) {
        switch (key) {
            case "credito":
                return new CreditoProcessor();
            case "debito":
                return new DebitoProcessor();
            case "voucher":
                return new VoucherProcessor();
            default:
                return null;
        }
    }
}
