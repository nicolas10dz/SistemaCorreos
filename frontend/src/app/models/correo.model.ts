export interface CorreoRequest {
  destinatario: string;
  asunto: string;
  mensaje: string;
}

export interface CorreoResponse {
  mensaje: string;
}
