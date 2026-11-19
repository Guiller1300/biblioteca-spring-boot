package com.arias.biblioteca_arias13.prestamo;

import com.arias.biblioteca_arias13.libro.Libro;
import com.arias.biblioteca_arias13.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "prestamos")
@Data
@NoArgsConstructor
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private LocalDate fechaPrestamo;

    private LocalDate fechaDevolucion;

    public Prestamo(Long id, Libro libro, Usuario usuario, LocalDate fechaPrestamo) {
        this.id = id;
        this.libro = libro;
        this.usuario = usuario;
        this.fechaPrestamo = fechaPrestamo;
    }
}
