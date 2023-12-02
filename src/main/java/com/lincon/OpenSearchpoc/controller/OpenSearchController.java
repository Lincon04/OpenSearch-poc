package com.lincon.OpenSearchpoc.controller;

import com.lincon.OpenSearchpoc.controller.filter.SaleFilter;
import com.lincon.OpenSearchpoc.dto.Sale;
import com.lincon.OpenSearchpoc.service.SaleService;
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
    public Sale get(String id) {
        return saleService.findByIdUsingGetRequest(id);
    }

    @GetMapping("/put")
    public String put() throws IOException {
        return saleService.saveAll(saleService.loadSales());
    }

    @GetMapping("/getv1")
    public String getv1() throws IOException {
        List<Sale> sales = saleService.loadSales();
        return saleService.save(sales.get(0));
    }

    @GetMapping("/getv2")
    public Sale getv2() throws IOException {
        List<Sale> sales = saleService.loadSales();
        return  saleService.findByIdUsingSearch(sales.get(0).getNsu().toString());
    }

    @GetMapping("/getv3")
    public String getv3() throws IOException {
        List<Sale> sales = saleService.loadSales();
        return saleService.update(sales.get(0));
    }

    @GetMapping("/getv4")
    public String getv4() throws IOException {
        List<Sale> sales = saleService.loadSales();
        return  saleService.delete(sales.get(0));
    }

    @GetMapping("/getv5")
    public List<Sale> getv5() throws IOException {
        return  saleService.findAll();
    }

    @GetMapping("/getRange")
    public Sale getPvNsu(SaleFilter saleFilter){
        return saleService.findByRangeSearch(saleFilter);
    }

    @GetMapping("/getv6")
    public Sale getv6(SaleFilter saleFilter) {
        return  saleService.findAllBy(saleFilter);
    }
}
