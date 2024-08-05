package com.lincon.OpenSearchpoc.reflection;

import org.opensearch.client.opensearch._types.aggregations.Aggregation;

import java.util.Map;

public interface SummarizeXpto<T> {

    default Map<String, Aggregation> aggregate(T filter) throws IllegalAccessException {
        SummarizeHandler<T> summarizeHandler = new SummarizeHandler<>();
        return summarizeHandler.aggregate(filter);
    }
}
