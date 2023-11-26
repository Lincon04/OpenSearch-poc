package com.lincon.OpenSearchpoc.controller;

import com.lincon.OpenSearchpoc.controller.filter.SaleFilter;
import com.lincon.OpenSearchpoc.dto.Sale;
import com.lincon.OpenSearchpoc.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/sale")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @PostMapping
    public String save(@RequestBody Sale sale) {
        return saleService.save(sale);
    }

    @GetMapping("/list")
    public List<Sale> list() throws IOException {
        return saleService.findAll();
    }

    @GetMapping("/get")
    public Sale get(String nsu){
        return saleService.findByIdUsingSearch(nsu);
    }

    @GetMapping("/getAll")
    public Sale getAll(SaleFilter saleFilter){
        return saleService.findAll(saleFilter);
    }

    @GetMapping("/getByRange")
    public Sale getByRange(SaleFilter saleFilter){
        return saleService.findByRange(saleFilter);
    }


}
