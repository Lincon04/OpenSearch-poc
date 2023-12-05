package com.lincon.OpenSearchpoc.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SaleResponse {
    List<Sale> sales = new ArrayList<>();
}
