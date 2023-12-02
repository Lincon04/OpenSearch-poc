package com.lincon.OpenSearchpoc.reflection;

import com.lincon.OpenSearchpoc.controller.filter.SaleFilter;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Play {

    public static Map<String, FieldValue> fieldValueMap = new HashMap<>();

    public static void main(String[] args) {
        SaleFilter saleFilter = new SaleFilter();
        saleFilter.setPv("651615");
        saleFilter.setNsu(65464L);
        saleFilter.setStartDataVenda("2023-03-06");

        CancellationFilter cancellationFilter = CancellationFilter.builder()
                .noticeNumber("161565161").nsu(156198L).type(2).cancellationDate("2023-03-05").build();

        validador(saleFilter);
        validador(cancellationFilter);

    }

    public static <T> void validador(T objeto) {
        Class<?> clazz = objeto.getClass();
        for (Field field: clazz.getDeclaredFields()){
            if(field.isAnnotationPresent(Searchable.class)){
                field.setAccessible(true);
                Searchable searchable = field.getAnnotation(Searchable.class);
                String attribute = field.getName();
                FieldValue fieldValue = getFieldValue(field, objeto);
                if(fieldValue != null){
                    fieldValueMap.put(attribute, fieldValue);
                    System.out.println(searchable.attributeName() + ": " + fieldValue);
                }
            }
        }
    }

    private static <T> FieldValue getFieldValue(Field field, T objeto){
        try {
            if (field.get(objeto) instanceof String){
                return FieldValue.of((String) field.get(objeto));
            }
            if (field.get(objeto) instanceof Long){
                return FieldValue.of((Long) field.get(objeto));
            }
            if (field.get(objeto) instanceof Double){
                return FieldValue.of((Double) field.get(objeto));
            }

        }catch (IllegalAccessException illegalAccessException){
            System.out.println(illegalAccessException.getCause());
        }
        return null;
    }

    private static MatchQuery getMatchQuery(String attribute, FieldValue fieldValue){
        return MatchQuery.of(match -> match.field(attribute).query(fieldValue));
    }
}
