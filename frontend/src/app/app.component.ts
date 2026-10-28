import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CorreoService } from './services/correo.service';
import { CorreoRequest } from './models/correo.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'Sistema de envío de correos';
  correoForm: FormGroup;
  enviando = false;
  mensajeExito = '';
  mensajeError = '';

  constructor(private fb: FormBuilder, private correoService: CorreoService) {
    this.correoForm = this.fb.group({
      destinatario: ['', [Validators.required, Validators.email]],
      asunto: ['', [Validators.required]],
      mensaje: ['', [Validators.required]]
    });
  }

  get f() {
    return this.correoForm.controls;
  }

  enviarCorreo() {
    this.mensajeExito = '';
    this.mensajeError = '';

    if (this.correoForm.invalid) {
      this.correoForm.markAllAsTouched();
      return;
    }

    if (this.enviando) {
      return;
    }

    this.enviando = true;
    const datos: CorreoRequest = this.correoForm.value;

    this.correoService.enviarCorreo(datos).subscribe({
      next: (response) => {
        this.enviando = false;
        this.mensajeExito = response.mensaje || 'Correo enviado correctamente';
        this.correoForm.reset();
      },
      error: (error) => {
        this.enviando = false;
        this.mensajeError = error.error?.mensaje || 'Error al enviar el correo. Inténtelo de nuevo.';
      }
    });
  }
}
