package com.proyecto.email.service;

import com.proyecto.email.dto.CorreoRequest;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudflareWorkerServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private CloudflareWorkerService cloudflareWorkerService;

    @BeforeEach
    void setUp() {
        cloudflareWorkerService = new CloudflareWorkerService(restClient, "http://localhost:8787", "token-test");
    }

    @Test
    void enviarCorreoExitoso() {
        CorreoRequest request = new CorreoRequest("test@example.com", "Asunto", "Mensaje");

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.header(any(String.class), any(String.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Object.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(org.springframework.http.ResponseEntity.ok().build());

        cloudflareWorkerService.enviarCorreo(request);

        verify(restClient).post();
    }

    @Test
    void enviarCorreoLanzaUnauthorizedCuando401() {
        CorreoRequest request = new CorreoRequest("test@example.com", "Asunto", "Mensaje");

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.header(any(String.class), any(String.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Object.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenAnswer(invocation -> {
            throw HttpClientErrorException.create(HttpStatus.UNAUTHORIZED, "No autorizado", HttpHeaders.EMPTY, new byte[0], StandardCharsets.UTF_8);
        });

        assertThrows(HttpClientErrorException.class, () -> cloudflareWorkerService.enviarCorreo(request));
    }
}
