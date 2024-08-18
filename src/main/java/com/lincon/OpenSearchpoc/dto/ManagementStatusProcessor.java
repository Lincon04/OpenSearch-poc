package com.lincon.OpenSearchpoc.dto;

import org.opensearch.client.opensearch._types.aggregations.Aggregate;
import org.opensearch.client.opensearch._types.aggregations.StringTermsBucket;

import java.util.List;

public class ManagementStatusProcessor implements ManagementProcessor {

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

    @Override
    public AggregationProcessor getProcessor(String key) {
        switch (key){
            case "pago":
                return new AggregationPaidProcessor();
            case "devolvido" :
                return new AggregationRefundProcessor();
            default:
                return null;
        }

    }
}
