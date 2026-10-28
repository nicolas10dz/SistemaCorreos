package com.proyecto.email.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.email.dto.CorreoRequest;
import com.proyecto.email.exception.GlobalExceptionHandler;
import com.proyecto.email.service.CloudflareWorkerService;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CorreoController.class)
@Import(GlobalExceptionHandler.class)
class CorreoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CloudflareWorkerService cloudflareWorkerService;

    @Test
    void enviarCorreoExitoso() throws Exception {
        CorreoRequest request = new CorreoRequest("test@example.com", "Asunto", "Mensaje");
        doNothing().when(cloudflareWorkerService).enviarCorreo(any(CorreoRequest.class));

        mockMvc.perform(post("/api/correos/enviar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Correo enviado correctamente"));
    }

    @Test
    void enviarCorreoConDatosInvalidos() throws Exception {
        CorreoRequest request = new CorreoRequest("", "", "");

        mockMvc.perform(post("/api/correos/enviar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Datos inválidos"));
    }

    @Test
    void enviarCorreoConCorreoInvalido() throws Exception {
        CorreoRequest request = new CorreoRequest("correo-invalido", "Asunto", "Mensaje");

        mockMvc.perform(post("/api/correos/enviar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Datos inválidos"));
    }

    @Test
    void enviarCorreoConError401() throws Exception {
        CorreoRequest request = new CorreoRequest("test@example.com", "Asunto", "Mensaje");
        doThrow(HttpClientErrorException.create(HttpStatus.UNAUTHORIZED, "No autorizado", HttpHeaders.EMPTY, new byte[0], StandardCharsets.UTF_8))
                .when(cloudflareWorkerService).enviarCorreo(any(CorreoRequest.class));

        mockMvc.perform(post("/api/correos/enviar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("No autorizado"));
    }

    @Test
    void healthEndpoint() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("OK"));
    }
}
