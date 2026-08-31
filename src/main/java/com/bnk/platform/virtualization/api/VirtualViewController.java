package com.bnk.platform.virtualization.api;

import com.bnk.platform.virtualization.domain.CustomerSearchRequest;
import com.bnk.platform.virtualization.domain.CustomerView;
import com.bnk.platform.virtualization.service.VirtualCustomerService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 향후 MCP tool(customer_search)의 호출 대상으로 전환할 REST 경계. */
@RestController
@RequestMapping("/api/v1/virtual-views/customers")
public class VirtualViewController {
    private final VirtualCustomerService service;
    public VirtualViewController(VirtualCustomerService service) { this.service = service; }
    @PostMapping("/search")
    public ResponseEntity<List<CustomerView>> search(@Valid @RequestBody CustomerSearchRequest request) {
        return ResponseEntity.ok(service.findByCustomerId(request.customerId()));
    }
}
