package com.bnk.platform.virtualization.api;

import com.bnk.platform.virtualization.domain.ProductSearchRequest;
import com.bnk.platform.virtualization.domain.ProductView;
import com.bnk.platform.virtualization.service.VirtualProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 향후 MCP tool(customer_search)의 호출 대상으로 전환할 REST 경계. */
@RestController
@RequestMapping("/api/v1/virtual-views/products")
public class VirtualViewController {
    private final VirtualProductService service;
    public VirtualViewController(VirtualProductService service) { this.service = service; }
    @PostMapping("/search")
    public ResponseEntity<List<ProductView>> search(@Valid @RequestBody ProductSearchRequest request) {
        return ResponseEntity.ok(service.findByItemName(request.itemName()));
    }
}
