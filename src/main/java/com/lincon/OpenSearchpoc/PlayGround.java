package com.lincon.OpenSearchpoc;

import com.lincon.OpenSearchpoc.controller.filter.SaleFilter;
import org.opensearch.client.opensearch._types.FieldValue;

import java.lang.reflect.Field;

public class PlayGround {

    public static void main(String[] args) throws IllegalAccessException, NoSuchFieldException {
        SaleFilter saleFilter = new SaleFilter();
        saleFilter.setPv("651615");
        saleFilter.setNsu(65464L);
        saleFilter.setStartDataVenda("2023-03-06");
        Class<?> c = saleFilter.getClass();
        Field[] fields = c.getDeclaredFields();
        for (Field f: fields){
            f.setAccessible(true);
            FieldValue fieldValue = null;
            if (f.get(saleFilter) instanceof String){
                System.out.println(f.getName()+ ": " + f.get(saleFilter) + " String");
                fieldValue = FieldValue.of((String) f.get(saleFilter));
            }
            if (f.get(saleFilter) instanceof Long){
                System.out.println(f.getName()+ ": " + f.get(saleFilter) + " Long");
                fieldValue = FieldValue.of((Long) f.get(saleFilter));
            }
            if (f.get(saleFilter) instanceof Double){
                System.out.println(f.getName()+ ": " + f.get(saleFilter) + " Double");
                fieldValue = FieldValue.of((Double) f.get(saleFilter));
            }

        }

    }
}
