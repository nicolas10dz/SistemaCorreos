package com.proyecto.email.service;

import com.proyecto.email.dto.CorreoRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

@Service
public class CloudflareWorkerService {

    private final RestClient restClient;
    private final String workerUrl;
    private final String workerToken;

    public CloudflareWorkerService(RestClient restClient,
                                   @Value("${app.worker.url}") String workerUrl,
                                   @Value("${app.worker.token}") String workerToken) {
        this.restClient = restClient;
        this.workerUrl = workerUrl;
        this.workerToken = workerToken;
    }

    public void enviarCorreo(CorreoRequest request) {
        try {
            restClient.post()
                    .uri(workerUrl)
                    .header("X-Worker-Token", workerToken)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        int status = res.getStatusCode().value();
                        if (status == 401) {
                            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "No autorizado");
                        }
                        if (status == 429) {
                            throw new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS, "Demasiadas solicitudes");
                        }
                        throw new HttpClientErrorException(res.getStatusCode(), "Error en la solicitud del cliente");
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        int status = res.getStatusCode().value();
                        if (status == 502) {
                            throw new HttpServerErrorException(HttpStatus.BAD_GATEWAY, "Bad Gateway");
                        }
                        throw new HttpServerErrorException(res.getStatusCode(), "Error en el servidor de Cloudflare");
                    })
                    .toBodilessEntity();
        } catch (HttpClientErrorException ex) {
            throw ex;
        } catch (HttpServerErrorException ex) {
            throw ex;
        }
    }
}