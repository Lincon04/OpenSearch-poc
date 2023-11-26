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
    private String data_venda;

    @JsonProperty("nsu")
    private Long nsu;

    @JsonProperty("rv_number")
    private Long rv_number;

    @JsonProperty("modalidade")
    private String modalidade;

    @JsonProperty("meio_de_pagamento")
    private String meio_pagamento;

    @JsonProperty("maquininha")
    private String maquininha;

    @JsonProperty("data_recebimento")
    private String data_recebimento;


}
