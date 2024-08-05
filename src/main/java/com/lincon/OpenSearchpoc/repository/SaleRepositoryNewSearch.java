package com.lincon.OpenSearchpoc.repository;

import com.lincon.OpenSearchpoc.controller.filter.SaleFilter;
import com.lincon.OpenSearchpoc.dto.Sale;
import com.lincon.OpenSearchpoc.dto.SaleResponse;
import com.lincon.OpenSearchpoc.reflection.SearchableQuery;
import com.lincon.OpenSearchpoc.reflection.SummarizeHandler;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.aggregations.*;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.util.ObjectBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Repository
public class SaleRepositoryNewSearch {

    @Autowired
    private OpenSearchClient openSearchClient;

    private static final String INDEX = "sales";


    public SaleResponse find(SaleFilter saleFilter) throws IOException {
        SearchableQuery<SaleFilter> searchable = new SearchableQuery<>(saleFilter);
        Query query = searchable.findAll();
        SummarizeHandler<SaleFilter> summarizeHandler = new SummarizeHandler<>();

        SearchRequest searchRequest = new SearchRequest.Builder().index(INDEX).query(query)
                .aggregations(summarizeHandler.aggregate(saleFilter)).build();

        SearchResponse<Sale> response = openSearchClient.search(searchRequest, Sale.class);
        SaleResponse saleResponse = new SaleResponse();
        response.aggregations().forEach(summaryResponse(saleResponse));

        if(!response.hits().hits().isEmpty()){
            for (Hit<Sale> hits: response.hits().hits()){
                saleResponse.getSales().add(hits.source());
            }
        }

        return saleResponse;
    }

    private static BiConsumer<String, Aggregate> summaryResponse(SaleResponse saleResponse) {
        return (s, aggregate) -> {
            String value = aggregate.sum().valueAsString();
            switch (s) {
                case "valor_bruto_total":
                    saleResponse.setSomaValorBruto(value);
                    break;
                case "desconto_total":
                    saleResponse.setSomaDesconto(value);
                    break;
                case "valor_liquido_total":
                    saleResponse.setSomaValorLiquido(value);
                    break;
            }
        };
    }


    public SaleResponse findAgg(SaleFilter saleFilter) throws IOException {
        SearchableQuery<SaleFilter> searchable = new SearchableQuery<>(saleFilter);
        Query query = searchable.findAll();
//        SummarizeHandler<SaleFilter> summarizeHandler = new SummarizeHandler<>();


        SearchRequest searchRequest = new SearchRequest.Builder().index(INDEX).query(query)
                .aggregations(saleFilter.handlerAggregation()).build();

        SearchResponse<Sale> response = openSearchClient.search(searchRequest, Sale.class);
        SaleResponse saleResponse = new SaleResponse();

        response.aggregations().get(saleFilter.getGroupBy()).lterms().buckets().array().forEach(data_aggr -> {
            System.out.println(data_aggr.keyAsString());
            data_aggr.aggregations().forEach((s, aggregate) -> {
                if(aggregate.isLterms()){
                    aggregate.lterms().buckets().array().forEach(data_rece -> {
                        System.out.println(s);
                        data_rece.aggregations().forEach(summaryResponse(saleResponse));
                    });
                }

                if(aggregate.isSterms()){
                    aggregate.sterms().buckets().array().forEach(modalidade -> {
                        System.out.println(s);
                        modalidade.aggregations().forEach(summaryResponse(saleResponse));
                    });
                }
            });
        });

        if(!response.hits().hits().isEmpty()){
            for (Hit<Sale> hits: response.hits().hits()){
                saleResponse.getSales().add(hits.source());
            }
        }

        return saleResponse;
    }

    public List<Hit<Sale>> findAll(SaleFilter saleFilter) throws IOException, IllegalAccessException {
        SearchableQuery<SaleFilter> searchable = new SearchableQuery<>(saleFilter);
        Query query = searchable.findAll();
        SummarizeHandler<SaleFilter> summarizeHandler = new SummarizeHandler<>();

        List<String> aggrs = Arrays.asList("data_venda", "modalidade", "maquininha", "total_vendas");
        List<Aggregation.Builder.ContainerBuilder> containerBuilderList = new ArrayList<>();
        for(String field: aggrs){
            Aggregation.Builder builder = new Aggregation.Builder();
            containerBuilderList.add(builder.terms(TermsAggregation.of(t -> t.field(field))));
        }

        Aggregation.Builder.ContainerBuilder lastContainerBuilder = getLastAggregation(aggrs, containerBuilderList);
        Aggregation aggregation = lastContainerBuilder.aggregations(summarizeHandler.aggregate(saleFilter)).build();

//        Aggregation.Builder builder = new Aggregation.Builder();
//        Aggregation.Builder.ContainerBuilder containerBuilder = builder.terms(TermsAggregation.of(t -> t.field("data_venda")));
//        addDataVendaAggregation(containerBuilder, aggrs, 0, saleFilter);
//
        SearchRequest searchRequest = new SearchRequest.Builder().index(INDEX).query(query)
                .aggregations("data_venda", aggregation).build();


//        SearchRequest searchRequest = new SearchRequest.Builder().index(INDEX).query(query)
//                .aggregations("data_venda", Aggregation.of(a -> a.terms(TermsAggregation.of(t -> t.field("data_venda")))
//                                .aggregations("modalidade", Aggregation.of(a1 -> a1.terms(TermsAggregation.of(t1 -> t1.field("modalidade")))
//                                        .aggregations(summarizeHandler.aggregate(saleFilter)))
//                                )
//                        )
//                ).build();


        SearchResponse<Sale> response = openSearchClient.search(searchRequest, Sale.class);

        response.aggregations().forEach(getBiConsumer());

        return response.hits().hits();
    }

    private static BiConsumer<String, Aggregate> getBiConsumer() {
        return (s, aggregate) -> {
            if (aggregate.isLterms()) {
                LongTermsAggregate longTermsAggregate = aggregate.lterms();
                longTermsAggregate.buckets().array().forEach(getLongTermsBucketConsumer(s));
            }
        };
    }

    private static Consumer<LongTermsBucket> getLongTermsBucketConsumer(String s) {
        return t -> {
            System.out.println(s + " -> " + t.keyAsString());
            t.aggregations().forEach(getAggregateBiConsumer());
        };
    }

    private static BiConsumer<String, Aggregate> getAggregateBiConsumer() {
        return (s1, aggregate1) -> {
            if (aggregate1.isSterms()) {
                StringTermsAggregate stringTermsAggregate = aggregate1.sterms();
                stringTermsAggregate.buckets().array().forEach(getStringTermsBucketConsumer(s1));
            }
        };
    }

    private static Consumer<StringTermsBucket> getStringTermsBucketConsumer(String s1) {
        return stringTermsBucket -> {
            System.out.println(" " + s1 + " -> " + stringTermsBucket.key());
            stringTermsBucket.aggregations().forEach(getStringAggregateBiConsumer());
        };
    }

    private static BiConsumer<String, Aggregate> getStringAggregateBiConsumer() {
        return (s2, aggregate2) -> {
            if (aggregate2.isSum()) {
                System.out.println("  " + s2 + " -> " + aggregate2.sum().valueAsString());
            }
        };
    }

    public Aggregation.Builder.ContainerBuilder getLastAggregation(List<String> aggrs, List<Aggregation.Builder.ContainerBuilder> containerBuilderList){
        Aggregation.Builder.ContainerBuilder lastContainer=null;
        for (int i = 1; i <= aggrs.size()-1; i++){
            Aggregation.Builder.ContainerBuilder containerBuilder = containerBuilderList.get(i);
            String field = aggrs.get(i);
            lastContainer = addSubAggr(containerBuilder, field);
        }
        return lastContainer;
    }

    public Aggregation.Builder.ContainerBuilder addSubAggr(Aggregation.Builder.ContainerBuilder agrregation, String field){
        return agrregation.aggregations(field, buildFunction(field));
    }

    public Function<Aggregation.Builder, ObjectBuilder<Aggregation>> buildFunction(String field){
        return builder -> builder.terms(t-> t.field(field));
    }

    public void addDataVendaAggregation(Aggregation.Builder.ContainerBuilder containerBuilder, List<String> aggrs, int count, SaleFilter saleFilter) {
        Aggregation.Builder builder = new Aggregation.Builder();
        int finalCount = count;
        String field = aggrs.get(finalCount);
        Aggregation.Builder.ContainerBuilder containerBuilder1 = builder.terms(TermsAggregation.of(t -> t.field(field)));

        if (aggrs.size() - 1 != count) {
            addDataVendaAggregation(containerBuilder1, aggrs, ++count, saleFilter);
        }
        if (field.equals("total_vendas")) {
            SummarizeHandler<SaleFilter> summarizeHandler = new SummarizeHandler<>();
            containerBuilder.aggregations(summarizeHandler.aggregate(saleFilter));
        } else {
            containerBuilder.aggregations(field, containerBuilder1.build());
        }
    }


//    public Map<String, Aggregation> percorreMap(Map<String, Aggregation> aggregationMap, String key, String secondKey){
//
//        Aggregation aggregation = aggregationMap.get(key);
//        Aggregation aggregation2 = aggregationMap.get(secondKey);
//        aggregation.aggregations().put(secondKey, aggregation2);
//    }

//    public Aggregation generateAggregations(Aggregation.Builder builder, List<String> fields, int index) {
//
//        if (index >= fields.size()) {
//            // Condição de parada: chegamos ao fim da lista de campos
//            return builder.terms(t -> t.field(fields.get(index - 1))).build(); // Retorna a última Aggregation
//        } else {
//            String currentField = fields.get(index);
//            return builder.terms(t -> t.field(currentField)).build().aggregations();
//
//                    //.aggregations(currentField, generateAggregations(builder, fields, index + 1));
//        }
//    }

//    public Aggregation.Builder.ContainerBuilder terms(Function<TermsAggregation.Builder, ObjectBuilder<TermsAggregation>> fn) {
//        return this.terms((TermsAggregation)((ObjectBuilder)fn.apply(new TermsAggregation.Builder())).build());
//    }

//    Aggregation.Builder builder = new Aggregation.Builder();
//    Aggregation.Builder.ContainerBuilder containerBuilder = builder.terms(TermsAggregation.of(t -> t.field("data_venda")));
//
//    Aggregation.Builder builder1 = new Aggregation.Builder();
//    Aggregation.Builder.ContainerBuilder containerBuilder1 = builder1.terms(TermsAggregation.of(t -> t.field("modalidade")));
//
//
//    Aggregation.Builder builder2 = new Aggregation.Builder();
//    Aggregation.Builder.ContainerBuilder containerBuilder2 = builder2.terms(TermsAggregation.of(t -> t.field("total_vendas")));
//
//        containerBuilder.aggregations("modalidade", containerBuilder1.aggregations("total_vendas", containerBuilder2.build()).build());

}


