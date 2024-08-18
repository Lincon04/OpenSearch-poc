package com.lincon.OpenSearchpoc.dto;

import org.opensearch.client.opensearch._types.aggregations.StringTermsBucket;

public interface AggregationProcessor {
    void process(StringTermsBucket bucket, SaleResponse saleResponse);
}
