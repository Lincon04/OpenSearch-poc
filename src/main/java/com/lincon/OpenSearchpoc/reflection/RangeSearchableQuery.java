package com.lincon.OpenSearchpoc.reflection;

import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.Query;

import java.lang.reflect.Field;

public class RangeSearchableQuery {
    private RangeSearchable rangeSearchable;
    private String attributeName;
    private JsonData startDate;
    private JsonData endDate;

    protected void prepareFieldsForRangeQuery(Field field, FieldValue value){
        rangeSearchable = field.getAnnotation(RangeSearchable.class);
        updateAttributeName();
        updateStartDateOrEndDate(value);
    }

    protected void updateAttributeName() {
        if(attributeName == null || attributeNameIsValid()){
            attributeName = rangeSearchable.attributeName();
        }
    }

    private boolean attributeNameIsValid() {
        if(!attributeName.equals(rangeSearchable.attributeName())){
            throw new RuntimeException("Os atributoName precisam ser iguais");
        }
        return true;
    }

    protected void updateStartDateOrEndDate(FieldValue attributeValue) {
        if(rangeSearchable.baseDate().equals(PointDateEnum.START)){
            startDate = JsonData.of(attributeValue);
        }else {
            endDate = JsonData.of(attributeValue);
        }
    }
    protected Query buildRangeQuery() {
        if (startDate != null && endDate != null){
            return buildQueryBetween();
        }else if(startDate != null) {
            return buildQueryGreaterThanOrEqual();
        }else if(endDate != null){
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
