package com.lincon.OpenSearchpoc.controller.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;

import java.util.ArrayList;
import java.util.List;

@Data
public class SaleFilter {

    @JsonProperty("pv")
    private String pv;

    @JsonProperty("data_venda")
    private String dataVenda;

    @JsonProperty("nsu")
    private Long nsu;

    @JsonProperty("rv_number")
    private Long rvNumber;

    @JsonProperty("modalidade")
    private String modalidade;

    @JsonProperty("meio_de_pagamento")
    private String meioPagamento;

    @JsonProperty("maquininha")
    private String maquininha;

    @JsonProperty("data_recebimento")
    private String dataRecebimento;


    private MatchQuery getPvMatchQuery(){
        return MatchQuery.of(match-> match.field("pv").query(FieldValue.of(this.pv)));
    }

    private MatchQuery getDataVendaMatchQuery(){
        return MatchQuery.of(match -> match.field("data_venda").query(FieldValue.of(this.dataVenda)));
    }

    private MatchQuery getNsuMatchQuery(){
        return MatchQuery.of(match -> match.field("nsu").query(FieldValue.of(this.nsu)));
    }

    private MatchQuery getRvNumberMatchQuery(){
        return MatchQuery.of(match -> match.field("rv_number").query(FieldValue.of(this.rvNumber)));
    }

    public Query createPvMatchQuery(){
        return Query.of(builder -> builder.match(this.getPvMatchQuery()));
    }

    public Query createDataVendaMatchQuery(){
        return Query.of(builder -> builder.match(this.getDataVendaMatchQuery()));
    }

    public Query createNsuMatchQuery(){
        return Query.of(builder -> builder.match(this.getNsuMatchQuery()));
    }

    public Query createRvNumberMatchQuery(){
        return Query.of(builder -> builder.match(this.getRvNumberMatchQuery()));
    }

    public List<Query> buildMatchQuery(){
        List<Query> queryList = new ArrayList<>();
        if(this.pv != null){
            queryList.add(this.createPvMatchQuery());
        }

        if(this.dataVenda != null){
            queryList.add(this.createDataVendaMatchQuery());
        }

        if(this.nsu != null){
            queryList.add(createNsuMatchQuery());
        }

        if(this.rvNumber != null){
            queryList.add(createRvNumberMatchQuery());
        }

        return queryList;
    }
}
