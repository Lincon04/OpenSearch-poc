package com.lincon.OpenSearchpoc.reflection;

import lombok.Setter;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.Query;

public class RangeSearchableClass {
    @Setter
    private RangeSearchable rangeSearchable;
    private String attributeName;
    private JsonData startDate;
    private JsonData endDate;

    protected void updateAttributeName() {
        if(this.attributeName == null || isValid()){
            this.attributeName = rangeSearchable.attributeName();
        }
    }

    private boolean isValid() {
        if(!this.attributeName.equals(this.rangeSearchable.attributeName())){
            throw new RuntimeException("Os atributoName precisam ser iguais");
        }
        return true;
    }

    protected void updateStartDateOrEndDate(FieldValue attributeValue) {
        if(this.rangeSearchable.baseDate().equals(BaseDateEnum.START)){
            this.startDate = JsonData.of(attributeValue);
        }else {
            this.endDate = JsonData.of(attributeValue);
        }
    }
    protected Query getRangeQuery() {
        if (this.startDate != null && this.endDate != null){
            return buildQueryBetween();
        }else if(this.startDate != null) {
            return buildQueryGreaterThanOrEqual();
        }else if(this.endDate != null){
            return buildQueryLessThanOrEqual();
        }
        return null;
    }

    private Query buildQueryBetween() {
        return Query.of(builder -> builder
                .range(range -> range.field(this.attributeName)
                        .gte(this.startDate)
                        .lte(this.endDate)))
                .range()._toQuery();
    }
    private Query buildQueryGreaterThanOrEqual() {
        return Query.of(builder -> builder
                .range(range -> range.field(this.attributeName)
                        .gte(this.startDate))).range()._toQuery();
    }

    private Query buildQueryLessThanOrEqual() {
        return Query.of(builder -> builder
                .range(range -> range.field(this.attributeName)
                        .gte(this.startDate))).range()._toQuery();
    }

}
