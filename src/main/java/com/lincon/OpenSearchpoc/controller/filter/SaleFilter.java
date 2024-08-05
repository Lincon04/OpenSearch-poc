package com.lincon.OpenSearchpoc.controller.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lincon.OpenSearchpoc.reflection.*;
import lombok.Data;
import org.opensearch.client.opensearch._types.aggregations.Aggregation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SaleFilter {

    @Searchable(attributeName = "pv")
    @JsonProperty("pv")
    private String pv;

    @JsonProperty("data_venda")
    @RangeSearchable(attributeName = "data_venda", baseDate = PointDateEnum.START)
    private String startDataVenda;

    @JsonProperty("data_venda")
    @RangeSearchable(attributeName = "data_venda", baseDate = PointDateEnum.END)
    private String endDataVenda;

    @JsonProperty("nsu")
    @Searchable(attributeName = "nsu")
    private Long nsu;

    @JsonProperty("rv_number")
    @Searchable(attributeName = "rv_number")
    private Long rvNumber;

    @JsonProperty("modalidade")
    @Searchable(attributeName = "modalidade")
    private String modalidade;

    @JsonProperty("meio_de_pagamento")
    @Searchable(attributeName = "meio_de_pagamento")
    private String meioPagamento;

    @JsonProperty("maquininha")
    @Searchable(attributeName = "maquininha")
    private String maquininha;

    @JsonProperty("data_recebimento")
    @Searchable(attributeName = "data_recebimento")
    private String dataRecebimento;

    @JsonProperty("valor_bruto")
    @Summarize(attributeName = "valor_bruto", aggregationName = "valor_bruto_total")
    private String valorBruto;

    @JsonProperty("desconto")
    @Summarize(attributeName = "desconto", aggregationName = "desconto_total")
    private String desconto;

    @JsonProperty("valor_liquido")
    @Summarize(attributeName = "valor_liquido", aggregationName = "valor_liquido_total")
    private String valorLiquido;

    @JsonProperty("groupBy")
    private String groupBy;

    @JsonProperty("innerGroup")
    private List<String> innerGroup;

    @JsonIgnore
    private final Map<String, Aggregation> subAggregations = new HashMap<>();

    @JsonIgnore
    private final Map<String, Aggregation> aggregations = new HashMap<>();

    public Map<String, Aggregation> handleInnerGroupBy() {
        SummarizeHandler<SaleFilter> summarizeHandler = new SummarizeHandler<>();
        for (String field : innerGroup) {
            subAggregations.put(field, Aggregation.of(builder -> builder.terms(terms -> terms.field(field))
                    .aggregations(summarizeHandler.aggregate(this))));
        }
        return subAggregations;
    }

    public Map<String, Aggregation> handlerAggregation(){
        aggregations.put(groupBy, Aggregation.of(a -> a.terms(t-> t.field(groupBy))
                .aggregations(handleInnerGroupBy())));
        return aggregations;
    }

}
