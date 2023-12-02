package com.lincon.OpenSearchpoc.controller.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lincon.OpenSearchpoc.reflection.BaseDateEnum;
import com.lincon.OpenSearchpoc.reflection.RangeSearchable;
import com.lincon.OpenSearchpoc.reflection.Searchable;
import lombok.Data;

@Data
public class SaleFilter {

    @Searchable(attributeName = "pv")
    @JsonProperty("pv")
    private String pv;

    @JsonProperty("data_venda")
    @RangeSearchable(attributeName = "data_venda", baseDate = BaseDateEnum.START)
    private String startDataVenda;

    @JsonProperty("data_venda")
    @RangeSearchable(attributeName = "data_venda", baseDate = BaseDateEnum.END)
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
}
