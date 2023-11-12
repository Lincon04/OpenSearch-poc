package com.lincon.OpenSearchpoc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Sale {

    @JsonProperty("pv")
    private String pv;

    @JsonProperty("data_venda")
    private String dataVenda;

    @JsonProperty("nsu")
    private Long nsu;

    @JsonProperty("rv_number")
    private Long rvNumber;

    @JsonProperty("valor_bruto")
    private Double valorBruto;

    @JsonProperty("desconto")
    private Double desconto;

    @JsonProperty("valor_liquido")
    private Double valorLiquido;

    @JsonProperty("modalidade")
    private String modalidade;

    @JsonProperty("meio_de_pagamento")
    private String meioPagamento;

    @JsonProperty("maquininha")
    private String maquininha;

    @JsonProperty("quantidade_parcelas")
    private Integer quantidadeParcelas;

    @JsonProperty("parcela")
    private Integer parcela;

    @JsonProperty("data_recebimento")
    private String dataRecebimento;

}
