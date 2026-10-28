package com.proyecto.email.controller;

import com.proyecto.email.dto.CorreoRequest;
import com.proyecto.email.dto.HealthResponse;
import com.proyecto.email.service.CloudflareWorkerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class CorreoController {

    private final CloudflareWorkerService cloudflareWorkerService;

    public CorreoController(CloudflareWorkerService cloudflareWorkerService) {
        this.cloudflareWorkerService = cloudflareWorkerService;
    }

    @PostMapping("/correos/enviar")
    public ResponseEntity<Map<String, String>> enviarCorreo(@Valid @RequestBody CorreoRequest request) {
        cloudflareWorkerService.enviarCorreo(request);
        return ResponseEntity.ok(Map.of("mensaje", "Correo enviado correctamente"));
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse("OK"));
    }
}
