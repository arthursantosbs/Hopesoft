package com.hopesoft.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityProbeController {

    @GetMapping("/admin/ping")
    public ResponseEntity<Map<String, String>> adminPing() {
        return ResponseEntity.ok(Map.of("area", "admin", "message", "acesso autorizado"));
    }

    @GetMapping("/operador/ping")
    public ResponseEntity<Map<String, String>> operadorPing() {
        return ResponseEntity.ok(Map.of("area", "operador", "message", "acesso autorizado"));
    }
}
