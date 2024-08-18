package com.lincon.OpenSearchpoc;

import com.lincon.OpenSearchpoc.controller.filter.SaleFilter;
import com.lincon.OpenSearchpoc.reflection.QueryDSL;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TestLIB {

    @Test
    public void testInitial(){
        List<String> innerGroupList = new ArrayList<>();
        innerGroupList.add("XPTO");
        innerGroupList.add("XYZ");
        SaleFilter saleFilter = new SaleFilter();
        saleFilter.setInnerGroup(innerGroupList);

        QueryDSL<SaleFilter> queryDSL = new QueryDSL<>(saleFilter);
        queryDSL.findAll();

    }
}
