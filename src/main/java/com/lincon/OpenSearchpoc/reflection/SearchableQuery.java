package com.lincon.OpenSearchpoc.reflection;

import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.Query;

import java.lang.reflect.Field;
import java.util.*;

public class SearchableQuery<T> extends RangeSearchableQuery {

    private final List<Query> queryList = new ArrayList<>();

    private final Map<String, FieldValue> searchableFields = new HashMap<>();

    private final T filter;

    public Query findAll() {
        buildDefaultQuery();
        addQuery(buildRangeQuery());
        return Query.of(query -> query.bool(bool -> bool.must(this.queryList))).bool()._toQuery();
    }

    public SearchableQuery(T filter){
        this.filter = filter;
        initialize(filter.getClass().getDeclaredFields());
    }

    private void initialize(Field[] fields) {
        for (Field field : fields) {
            FieldValue fieldValue = getFieldValue(field);
            if(fieldValue != null){
                if(field.isAnnotationPresent(Searchable.class)){
                    prepareFieldsForQuery(field, fieldValue);
                }
                if(field.isAnnotationPresent(RangeSearchable.class)){
                    prepareFieldsForRangeQuery(field, fieldValue);
                }
            }
        }
    }

    private void prepareFieldsForQuery(Field field, FieldValue fieldValue) {
        searchableFields.put(field.getAnnotation(Searchable.class).attributeName(), fieldValue);
    }


    private FieldValue getFieldValue(Field field) {
        field.setAccessible(true);
        try {
            if (field.get(this.filter) instanceof String) {
                return FieldValue.of((String) field.get(this.filter));
            }
            if (field.get(this.filter) instanceof Long) {
                return FieldValue.of((Long) field.get(this.filter));
            }
            if (field.get(this.filter) instanceof Double) {
                return FieldValue.of((Double) field.get(this.filter));
            }

        } catch (IllegalAccessException exception) {
            System.out.println(Arrays.toString(exception.getStackTrace()));
        }
        return null;
    }

    public void buildDefaultQuery(){
        this.searchableFields.forEach((attribute, value)->{
            addQuery(Query.of(builder -> builder.match(match -> match.field(attribute).query(value))));
        });
    }

    private void addQuery(Query query) {
        if (query != null) {
            this.queryList.add(query);
        }
    }

    private Query getQuery(String attribute, FieldValue value) {
        return Query.of(builder -> builder.match(match -> match.field(attribute).query(value)));
    }

}
