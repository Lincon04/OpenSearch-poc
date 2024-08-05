package com.lincon.OpenSearchpoc.reflection;

import org.opensearch.client.opensearch._types.aggregations.Aggregation;
import org.opensearch.client.opensearch._types.aggregations.AggregationBuilders;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SummarizeHandler<T> {

    private final Map<String, Aggregation> aggregations = new HashMap<>();

    public Map<String, Aggregation> aggregate(T filter)  {
        Field[] fields = filter.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Summarize.class)) {
                try {
                    if (!extracted(filter, field)) {
                        Summarize summarize = field.getAnnotation(Summarize.class);
                        aggregations.put(summarize.aggregationName(),
                                AggregationBuilders.sum()
                                        .field(summarize.attributeName())
                                        .format("0.00").build()._toAggregation());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return aggregations;
    }

    private static <T> boolean extracted(T filter, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        Object objectValue = field.get(filter);
        return objectValue == null;
    }


//    private FieldValue getFieldValue(Field field) {
//        field.setAccessible(true);
//        try {
//            Object fieldValue = field.get(this.filter);
//
//            if (fieldValue instanceof String) {
//                return FieldValue.of((String) fieldValue);
//            } else if (fieldValue instanceof Long) {
//                return FieldValue.of((Long) fieldValue);
//            } else if (fieldValue instanceof Double) {
//                return FieldValue.of((Double) fieldValue);
//            } else if (fieldValue instanceof List) {
//                List<FieldValue> fieldValues = new ArrayList<>();
//                for (Object item : (List<?>) fieldValue) {
//                    if (item instanceof String) {
//                        fieldValues.add(FieldValue.of((String) item));
//                    } else if (item instanceof Long) {
//                        fieldValues.add(FieldValue.of((Long) item));
//                    } else if (item instanceof Double) {
//                        fieldValues.add(FieldValue.of((Double) item));
//                    }
//                    // Add more type checks as needed
//                }
//                return FieldValue.of(fieldValues);
//            }
//        } catch (IllegalAccessException exception) {
//            System.out.println(Arrays.toString(exception.getStackTrace()));
//        }
//        return null;
//    }
}
