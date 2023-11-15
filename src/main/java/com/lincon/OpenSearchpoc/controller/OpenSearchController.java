package com.lincon.OpenSearchpoc.controller;

import com.lincon.OpenSearchpoc.dto.Sale;
import com.lincon.OpenSearchpoc.service.SaleService;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.core.bulk.BulkResponseItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/OpenSearchController")
public class OpenSearchController {

    @Autowired
    private SaleService saleService;

    @GetMapping("/get")
    public Sale get(String id) throws IOException {
        return saleService.findById(id);
    }

    @GetMapping("/put")
    public List<BulkResponseItem> put() throws IOException {

        return saleService.saveAll(saleService.loadSales());
    }

    @GetMapping("/getv1")
    public IndexResponse getv1() throws IOException {
        List<Sale> sales = saleService.loadSales();

        return  saleService.save(sales.get(0));
    }

    @GetMapping("/getv2")
    public Sale getv2() throws IOException {
        List<Sale> sales = saleService.loadSales();

        return  saleService.findById(sales.get(0).getNsu().toString(), sales.get(0));
    }

    @GetMapping("/getv3")
    public Sale getv3() throws IOException {
        List<Sale> sales = saleService.loadSales();

        return  saleService.update(sales.get(0), sales.get(1));
    }

    @GetMapping("/getv4")
    public boolean getv4() throws IOException {
        List<Sale> sales = saleService.loadSales();

        return  saleService.delete(sales.get(0));
    }
}
