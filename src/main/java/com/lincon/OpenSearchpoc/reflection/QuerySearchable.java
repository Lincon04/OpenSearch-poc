package com.lincon.OpenSearchpoc.reflection;

import lombok.AllArgsConstructor;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.Query;

import java.lang.reflect.Field;
import java.util.*;

@AllArgsConstructor
public class QuerySearchable<T> extends RangeSearchableClass {
    private final List<Query> queryList = new ArrayList<>();

    private final T filter;

    public Query findAll() {
        Class<?> clazz = filter.getClass();
        handleWithSearchable(clazz.getDeclaredFields());
        addQuery(getRangeQuery());
        return Query.of(query -> query.bool(bool -> bool.must(this.queryList))).bool()._toQuery();
    }

    private void handleWithSearchable(Field[] fields) {
        for (Field field : fields) {
            this.fieldToQuery(field);
        }
    }

    private void fieldToQuery(Field field) {
        field.setAccessible(true);
        FieldValue attributeValue = getFieldValue(field);
        if (attributeValue != null) {
            if (field.isAnnotationPresent(Searchable.class)) {
                addQueryFrom(field, attributeValue);
            } else if (field.isAnnotationPresent(RangeSearchable.class)) {
                updateRangeSearchableValues(field, attributeValue);
            }
        }
    }

    private FieldValue getFieldValue(Field field) {
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

    private void addQueryFrom(Field field, FieldValue attributeValue) {
        Searchable searchable = field.getAnnotation(Searchable.class);
        String attributeName = searchable.attributeName();
        addQuery(this.getQuery(attributeName, attributeValue));
    }

    private void addQuery(Query query) {
        if (query != null) {
            this.queryList.add(query);
        }
    }

    private void updateRangeSearchableValues(Field field, FieldValue attributeValue) {
        setRangeSearchable(field.getAnnotation(RangeSearchable.class));
        updateAttributeName();
        updateStartDateOrEndDate(attributeValue);
    }

    private Query getQuery(String attribute, FieldValue value) {
        return Query.of(builder -> builder.match(match -> match.field(attribute).query(value)));
    }

}
