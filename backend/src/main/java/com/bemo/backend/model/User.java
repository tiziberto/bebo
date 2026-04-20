package com.bemo.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuario",
       uniqueConstraints = @UniqueConstraint(name = "uq_usuario_username", columnNames = "username"))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    /** Nombre completo del usuario según el schema SQL. */
    @Column(name = "nombre", length = 100)
    private String nombre = "";

    /**
     * Nivel de acceso: 0=solo lectura, 5=operador, 9=admin.
     * Según el schema SQL.
     */
    @Column(name = "nivel")
    private Integer nivel = 0;

    /**
     * Hash de la contraseña. Campo Java "password" mapeado a columna
     * "password_hash" del schema SQL.
     */
    @Column(name = "password_hash", nullable = false)
    private String password;

    /**
     * Email — no existe como columna en la tabla "usuario".
     * @Transient: se mantiene en memoria para el código existente
     * pero JPA no lo persiste ni lo incluye en INSERT/UPDATE.
     */
    @Transient
    private String email;

    /**
     * Flag activo — no existe como columna en la tabla "usuario".
     * @Transient: siempre true en memoria; usar fecha_baja para
     * determinar si el usuario está inactivo.
     */
    @Transient
    private Boolean isActive = true;

    /** FK a tipo_usuario según el schema SQL. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_usuario_id")
    private TipoUsuario tipoUsuario;

    /** FK al prestador asociado según el schema SQL. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_id")
    private Profesional prestador;

    @Column(name = "fecha_alta")
    private LocalDate fechaAlta;

    @Column(name = "fecha_baja")
    private LocalDate fechaBaja;

    // ── Auditoría ────────────────────────────────────────────────
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50, updatable = false)
    private String createdBy = "";

    @Column(name = "updated_by", length = 50)
    private String updatedBy = "";

    /**
     * Roles del sistema (tabla extra "user_roles", mantenida para
     * compatibilidad con Spring Security).
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) this.createdAt = now;
        this.updatedAt = now;
        if (this.createdBy == null) this.createdBy = "";
        if (this.updatedBy == null) this.updatedBy = "";
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.updatedBy == null) this.updatedBy = "";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getNivel() { return nivel; }
    public void setNivel(Integer nivel) { this.nivel = nivel; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public Profesional getPrestador() { return prestador; }
    public void setPrestador(Profesional prestador) { this.prestador = prestador; }

    public LocalDate getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(LocalDate fechaAlta) { this.fechaAlta = fechaAlta; }

    public LocalDate getFechaBaja() { return fechaBaja; }
    public void setFechaBaja(LocalDate fechaBaja) { this.fechaBaja = fechaBaja; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
}