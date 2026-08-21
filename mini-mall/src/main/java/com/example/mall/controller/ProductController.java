package com.example.mall.controller;

import com.example.mall.entity.Product;
import com.example.mall.service.OrderService;
import com.example.mall.service.ProductService;
import com.example.mall.service.TxDemoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class ProductController {

    private final ProductService productService;
    private final OrderService orderService;
    private final TxDemoService txDemoService;

    public ProductController(ProductService productService, OrderService orderService,
                             TxDemoService txDemoService) {
        this.productService = productService;
        this.orderService = orderService;
        this.txDemoService = txDemoService;
    }

    // ---- Day 11 缓存实验 ----
    @GetMapping("/product/{id}")
    public Product get(@PathVariable Long id) throws Exception {
        return productService.getByIdWithCache(id);
    }

    // ---- Day 10 超卖实验（压测用） ----
    @PostMapping("/buy/wrong/{id}")
    public String buyWrong(@PathVariable Long id) {
        return orderService.buyWrong(id);
    }

    @PostMapping("/buy/atomic/{id}")
    public String buyAtomic(@PathVariable Long id) {
        return orderService.buyWithAtomicSql(id);
    }

    @PostMapping("/buy/version/{id}")
    public String buyVersion(@PathVariable Long id) {
        return orderService.buyWithVersion(id);
    }

    // ---- Day 8 事务失效实验 ----
    @GetMapping("/tx/self-call")
    public String txSelfCall() {
        return txDemoService.createWithSelfCall("自调用商品");
    }

    @GetMapping("/tx/swallow")
    public String txSwallow() {
        return txDemoService.createWithSwallowedException("吞异常商品");
    }

    @GetMapping("/tx/checked")
    public String txChecked() throws Exception {
        txDemoService.createWithCheckedException("checked异常商品");
        return "done";
    }
}
