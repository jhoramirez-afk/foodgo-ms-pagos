package cl.duoc.jv0101.foodgo.pagos.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;


@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El pedido es obligatorio")
    @Column(nullable = false)
    private String pedido;
    @Column
    private String metodo;
    @Column
    private BigDecimal monto;

    @Valid
    @OneToMany(mappedBy = "pago", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("pago-transacciones")
    private List<TransaccionPago> transacciones = new ArrayList<>();

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getPedido() { return pedido; }

    public void setPedido(String pedido) { this.pedido = pedido; }

    public String getMetodo() { return metodo; }

    public void setMetodo(String metodo) { this.metodo = metodo; }

    public BigDecimal getMonto() { return monto; }

    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public List<TransaccionPago> getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(List<TransaccionPago> items) {
        this.transacciones.clear();
        if (items != null) {
            items.forEach(this::addTransaccionPago);
        }
    }

    public void addTransaccionPago(TransaccionPago item) {
        transacciones.add(item);
        item.setPago(this);
    }

    public void removeTransaccionPago(TransaccionPago item) {
        transacciones.remove(item);
        item.setPago(null);
    }
}
