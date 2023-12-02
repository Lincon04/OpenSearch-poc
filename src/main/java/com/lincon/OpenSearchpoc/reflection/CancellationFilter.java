package com.lincon.OpenSearchpoc.reflection;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancellationFilter {

    @Searchable(attributeName="notice_number")
    private String noticeNumber;

    @Searchable(attributeName="nsu")
    private Long nsu;

    private String cancellationDate;

    private Integer type;
}
