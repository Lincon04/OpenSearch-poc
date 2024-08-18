package com.lincon.OpenSearchpoc.reflection;

import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.FieldValue;

import java.util.List;
import java.util.Objects;

public class QueryPromise {
    private final String attributeName;
    private FieldValue fieldValue;
    private List<FieldValue> fieldValues;
    private ConditionEnum conditionEnum;
    private JsonData initialData;
    private JsonData endData;

    public QueryPromise(Searchable searchable, FieldValue value){
        this.attributeName = searchable.attributeName();
        this.fieldValue = value;
        this.conditionEnum = searchable.condition();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryPromise that = (QueryPromise) o;
        return Objects.equals(attributeName, that.attributeName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(attributeName);
    }

    public void merge(QueryPromise other) {
        if(this.initialData==null){
            this.initialData = other.initialData;
        }else if(this.endData==null){
            this.endData = other.endData;
        }
    }
}
