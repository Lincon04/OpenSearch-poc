package com.lincon.OpenSearchpoc.dto;

import org.opensearch.client.opensearch._types.aggregations.Aggregate;
import org.opensearch.client.opensearch._types.aggregations.StringTermsBucket;

import java.util.List;

public interface ManagementProcessor {

    void management(Aggregate aggregate, SaleResponse saleResponse);
    void processInnerBuckets(List<StringTermsBucket> buckets, SaleResponse saleResponse);
    AggregationProcessor getProcessor(String key);
}
