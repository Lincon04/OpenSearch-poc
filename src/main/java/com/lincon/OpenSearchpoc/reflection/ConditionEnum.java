package com.lincon.OpenSearchpoc.reflection;

import lombok.Data;
import lombok.Getter;

@Getter
public enum ConditionEnum {
    EQUAL,
    GREATER_THAN,
    LESS_THAN,
    GREATER_THAN_OR_EQUAL_TO,
    LESS_THAN_OR_EQUAL_TO
}
