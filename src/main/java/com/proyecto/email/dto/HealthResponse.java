package com.proyecto.email.dto;

public class HealthResponse {
    private String estado;

    public HealthResponse() {
    }

    public HealthResponse(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
