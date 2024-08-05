package com.lincon.OpenSearchpoc.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SaleResponse {
    private List<Sale> sales = new ArrayList<>();
    private String somaValorBruto;
    private String somaValorLiquido;
    private String somaDesconto;
}
