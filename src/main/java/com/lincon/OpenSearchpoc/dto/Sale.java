package com.lincon.OpenSearchpoc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import lombok.Data;


import java.io.File;
import java.io.IOException;
import java.util.List;

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

    public List<Sale> loadSales() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new StdDateFormat());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        File file = new File("C:\\Dev\\projetos\\OpenSearch-poc\\src\\main\\resources\\static\\Sales.json");
        JavaType type = TypeFactory.defaultInstance().constructCollectionType(List.class, Sale.class);
        return mapper.readValue(file, type);
    }

}
