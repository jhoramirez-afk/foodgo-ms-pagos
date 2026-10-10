package cl.duoc.jv0101.foodgo.pagos.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "transacciones_pago")
public class TransaccionPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tipo de transacción es obligatorio")
    @Pattern(regexp = "COBRO|REEMBOLSO", message = "Tipo de transacción debe ser COBRO, REEMBOLSO")
    @Column(nullable = false)
    private String tipo;

    @NotNull(message = "Monto es obligatorio")
    @DecimalMin(value = "1", message = "Monto debe ser mayor que cero")
    @Digits(integer = 9, fraction = 0, message = "El importe debe expresarse en pesos CLP enteros, hasta 9 dígitos")
    @Column(precision = 9, scale = 0)
    private BigDecimal monto;

    @NotBlank(message = "Estado de transacción es obligatorio")
    @Pattern(regexp = "PENDIENTE|APROBADA|RECHAZADA", message = "Estado de transacción debe ser PENDIENTE, APROBADA, RECHAZADA")
    @Column(nullable = false)
    private String estado;

    @NotBlank(message = "Referencia es obligatorio")
    @Size(max = 255, message = "El campo admite hasta 255 caracteres")
    @Column(nullable = false)
    private String referencia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pago_id", nullable = false)
    @JsonBackReference("pago-transacciones")
    private Pago pago;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }
}
