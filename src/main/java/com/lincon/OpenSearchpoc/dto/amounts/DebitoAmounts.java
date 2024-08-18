package com.lincon.OpenSearchpoc.dto.amounts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DebitoAmounts {

    @JsonProperty("debitAmount")
    private String amount;

    @JsonProperty("debitNetAmount")
    private String netAmount;

    @JsonProperty("debitDescountAmount")
    private String descountAmount;
}
