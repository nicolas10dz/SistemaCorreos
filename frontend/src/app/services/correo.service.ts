import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CorreoRequest, CorreoResponse } from '../models/correo.model';

@Injectable({
  providedIn: 'root'
})
export class CorreoService {
  private apiUrl = 'https://sistemacorreos-2.onrender.com/api/correos/enviar';

  constructor(private http: HttpClient) { }

  enviarCorreo(correo: CorreoRequest): Observable<CorreoResponse> {
    return this.http.post<CorreoResponse>(this.apiUrl, correo);
  }
}
