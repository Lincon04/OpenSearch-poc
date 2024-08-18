package com.lincon.OpenSearchpoc.dto.amounts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoucherAmount {

    @JsonProperty("voucherAmount")
    private String mount;
}
