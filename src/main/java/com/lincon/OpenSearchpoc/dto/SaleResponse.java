package com.lincon.OpenSearchpoc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lincon.OpenSearchpoc.dto.amounts.CreditoAmount;
import com.lincon.OpenSearchpoc.dto.amounts.DebitoAmounts;
import com.lincon.OpenSearchpoc.dto.amounts.VoucherAmount;
import lombok.Data;
import org.opensearch.client.opensearch._types.aggregations.Aggregate;
import org.opensearch.client.opensearch._types.aggregations.LongTermsBucket;
import org.opensearch.client.opensearch._types.aggregations.StringTermsBucket;
import org.opensearch.client.opensearch.core.SearchResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaleResponse {
    private List<Sale> sales = new ArrayList<>();

    private CreditoAmount credito;
    private DebitoAmounts debitoAmounts;
    private VoucherAmount voucherAmount;

    private String paidAmount;
    private String paidNetAmount;
    private String paidDescountAmount;

    private String refundAmount;
    private String refundNetAmount;
    private String refundDescountAmount;


    public static SaleResponse fromSearchResponse(SearchResponse<Sale> response, String groupBy) {
        SaleResponse saleResponse = new SaleResponse();
        if (hasAggregations(response.aggregations())) {
            Aggregate aggregate = response.aggregations().get(groupBy);
            if (aggregate.isLterms()) {
                List<LongTermsBucket> buckets = aggregate.lterms().buckets().array();
                buckets.forEach(bucket -> processBucket(bucket, saleResponse));
            }
        }
        return saleResponse;
    }

    private static void processBucket(LongTermsBucket bucket, SaleResponse saleResponse) {
        if (hasAggregations(bucket.aggregations())) {
            bucket.aggregations().forEach((key, aggregate) -> {
                ManagementProcessor processor = getProcessor(key);
                if (processor != null) {
                    processor.management(aggregate, saleResponse);
                }
            });
        }
    }

    private static ManagementProcessor getProcessor(String innerGroup) {
        if (innerGroup.equals("modalidade")) {
            return new ManagementModalityProcessor();
        } else if (innerGroup.equals("status")) {
            return new ManagementStatusProcessor();
        } else {
            return null;
        }
    }

    private static void processInnerBuckets(List<StringTermsBucket> buckets, SaleResponse saleResponse) {
        buckets.forEach(bucket -> {
            AggregationProcessor processor = getModalityProcessor(bucket.key());
            if (processor != null) {
                processor.process(bucket, saleResponse);
            }
        });
    }

    private static AggregationProcessor getModalityProcessor(String key) {
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

    private static boolean hasAggregations(Map<String, Aggregate> aggregationMap) {
        return !aggregationMap.isEmpty();
    }
}
