package com.lincon.OpenSearchpoc.repository;

import com.lincon.OpenSearchpoc.controller.filter.SaleFilter;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.RangeQuery;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaleQueryBuilder {

    public SaleFilter saleFilter;

    private final Map<String, FieldValue> mapFields = new HashMap<>();

    private final List<Query> queryList = new ArrayList<>();

    public SaleQueryBuilder (SaleFilter saleFilter){
        this.saleFilter = saleFilter;
        Class<?> classSale = saleFilter.getClass();
        Field[] fieldsSale = classSale.getDeclaredFields();

        for(Field field: fieldsSale){
            field.setAccessible(true);
            String attribute = field.getName();
            FieldValue fieldValue = getFieldValue(field, saleFilter);
            if(fieldValue != null){
                mapFields.put(attribute, fieldValue);
            }
        }
        this.buildMatchQuery();
    }

    public Query findAll(){
        return Query.of(query -> query.bool(bool -> bool.must(this.queryList))).bool()._toQuery();
    }


    public Query findByRange(String start, String end){
        return Query.of(query -> query.range(getRangeQuery(start, end))).range()._toQuery();
    }

    public void buildMatchQuery(){
        this.mapFields.forEach((attribute, fieldValue)-> {
            Query query = createMatchQuery(attribute, fieldValue);
            this.queryList.add(query);
        });
    }

    private Query createMatchQuery(String attribute, FieldValue fieldValue){
        return Query.of(builder -> builder.match(this.getMatchQuery(attribute, fieldValue)));
    }

    private MatchQuery getMatchQuery(String attribute, FieldValue fieldValue){
        return MatchQuery.of(match -> match.field(attribute).query(fieldValue));
    }

    // Descobrir uma forma eficaz de usar saber qual o campo deve uar na busca por range
    private RangeQuery getRangeQuery(String startDate, String endDate){
        JsonData start = JsonData.of(startDate);
        JsonData end = JsonData.of(endDate);
        return RangeQuery.of(range -> range.field("data_venda").gte(start).lte(end))._toQuery().range();
    }

    private FieldValue getFieldValue(Field field, SaleFilter saleFilter){
       try {
            if (field.get(saleFilter) instanceof String){
                return FieldValue.of((String) field.get(saleFilter));
            }
            if (field.get(saleFilter) instanceof Long){
                return FieldValue.of((Long) field.get(saleFilter));
            }
            if (field.get(saleFilter) instanceof Double){
                return FieldValue.of((Double) field.get(saleFilter));
            }

       }catch (IllegalAccessException illegalAccessException){
           System.out.println(illegalAccessException.getCause());
       }
        return null;
    }

}
