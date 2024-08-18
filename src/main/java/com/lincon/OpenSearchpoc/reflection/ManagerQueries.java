package com.lincon.OpenSearchpoc.reflection;

import java.util.ArrayList;
import java.util.List;

public class ManagerQueries {

    private List<QueryPromise> queryPromiseList = new ArrayList<>();

    public void add(QueryPromise queryPromise){
        if(!queryIsPresent(queryPromise)){
            queryPromiseList.add(queryPromise);
        }
    }

    private boolean queryIsPresent(QueryPromise queryPromise) {
        for (QueryPromise searchingQuery: queryPromiseList){
            if(searchingQuery.equals(queryPromise)){
                searchingQuery.merge(queryPromise);
                return true;
            }
        }
        return false;
    }


}
