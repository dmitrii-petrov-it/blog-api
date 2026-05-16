package org.example.blogapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Test", description = "Test endpoints for security testing")
public class TestController {
    @Operation(summary = "Public endpoint", description = "Available without authentication")
    @GetMapping("/api/public/hello")
    public String publicHello(){
        return "Hello all";
    }

    @GetMapping("/api/private/hello")
    @Operation(summary = "Private endpoint", description = "Requires authentication")
    public String privateHello(Authentication auth){
        return "Hello " + auth.getName();
    }

    @GetMapping("/api/admin/hello")
    @Operation(summary = "Admin endpoint", description = "Requires ADMIN role")
    public String adminHello(Authentication auth){
        return "Hello admin " + auth.getName();
    }


}
