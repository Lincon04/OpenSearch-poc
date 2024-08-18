package com.lincon.OpenSearchpoc.dto;

import lombok.Data;
import org.opensearch.client.opensearch._types.aggregations.Aggregate;
import org.opensearch.client.opensearch._types.aggregations.DateHistogramBucket;
import org.opensearch.client.opensearch._types.aggregations.StringTermsBucket;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class SaleDailyResponse {
    private String keyDate;
    private String paidAmount;
    private String paidNetAmount;
    private String paidDescountAmount;
    private String refundAmount;
    private String refundNetAmount;
    private String refundDescountAmount;
    private String creditAmount;
    private String creditNetAmount;
    private String creditDescountAmount;
    private String debitAmount;
    private String debitNetAmount;
    private String debitDescountAmount;
    private String voucherAmount;
    private long count;
    private List<Sale> sales;
    private Integer nextKey;

    public void add(Sale sale){
        if(sales ==null){
            sales = new ArrayList<>();
        }
        sales.add(sale);
    }

    public static SaleDailyResponse create(String keyDate) {
        return new SaleDailyResponse(keyDate);
    }

    private SaleDailyResponse(String keyDate) {
        this.keyDate = keyDate;
    }

    public void dailyAggregation(DateHistogramBucket longTermsBucket) {
        longTermsBucket.aggregations().forEach((s, aggregate1) -> {
            aggregate1.sterms().buckets().array().forEach(stringTermsBucket -> handler(s, aggregate1));
        });
    }

    public void handler(String key, Aggregate aggregate) {
        if(key.equals("modalidade")){
            aggregate.sterms().buckets().array().forEach(this::handlerModalidade);
        }else if(key.equals("status")){
            aggregate.sterms().buckets().array().forEach(this::handlerStatus);
        }
    }

    public void handlerModalidade(StringTermsBucket stringTermsBucket) {
        switch (stringTermsBucket.key()){
            case "credito" -> handlerCredito(stringTermsBucket);
            case "debito" -> handlerDebito(stringTermsBucket);
            case "voucher" -> handlerVoucher(stringTermsBucket);
        }
    }

    public void handlerStatus(StringTermsBucket stringTermsBucket) {
        switch (stringTermsBucket.key()){
            case "paid" -> handlerPaid(stringTermsBucket);
            case "refund" -> handlerRefund(stringTermsBucket);
            case "cancelled" -> handlerCancelled(stringTermsBucket);
        }
    }

    private void handlerVoucher(StringTermsBucket stringTermsBucket) {
        stringTermsBucket.aggregations().forEach((s, aggregate) -> {
            if (s.equals("valor_bruto_total")) {
                voucherAmount = aggregate.sum().valueAsString();
            }
        });
    }

    private void handlerDebito(StringTermsBucket stringTermsBucket) {
        stringTermsBucket.aggregations().forEach((s, aggregate) -> {
            switch (s) {
                case "valor_bruto_total" -> debitAmount = aggregate.sum().valueAsString();
                case "desconto_total" -> debitDescountAmount = aggregate.sum().valueAsString();
                case "valor_liquido_total" -> debitNetAmount = aggregate.sum().valueAsString();
            }
        });
    }

    private void handlerCredito(StringTermsBucket stringTermsBucket) {
        stringTermsBucket.aggregations().forEach((s, aggregate) -> {
            switch (s) {
                case "valor_bruto_total" -> creditAmount = aggregate.sum().valueAsString();
                case "desconto_total" -> creditDescountAmount = aggregate.sum().valueAsString();
                case "valor_liquido_total" -> creditNetAmount = aggregate.sum().valueAsString();
            }
        });
    }

    private void handlerPaid(StringTermsBucket stringTermsBucket){
        stringTermsBucket.aggregations().forEach((s, aggregate) -> {
            switch (s) {
                case "valor_bruto_total" -> paidAmount = aggregate.sum().valueAsString();
                case "desconto_total" -> paidDescountAmount = aggregate.sum().valueAsString();
                case "valor_liquido_total" -> paidNetAmount = aggregate.sum().valueAsString();
            }
        });
    }

    private void handlerRefund(StringTermsBucket stringTermsBucket){
        stringTermsBucket.aggregations().forEach((s, aggregate) -> {
            switch (s) {
                case "valor_bruto_total" -> refundAmount = aggregate.sum().valueAsString();
                case "desconto_total" -> refundDescountAmount = aggregate.sum().valueAsString();
                case "valor_liquido_total" -> refundNetAmount = aggregate.sum().valueAsString();
            }
        });
    }

    private void handlerCancelled(StringTermsBucket stringTermsBucket){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaleDailyResponse that = (SaleDailyResponse) o;
        return Objects.equals(keyDate, that.keyDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(keyDate);
    }


}
