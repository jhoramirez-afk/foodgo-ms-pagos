package cl.duoc.jv0101.foodgo.pagos.service;

import java.util.List;
import java.math.BigDecimal;
import cl.duoc.jv0101.foodgo.pagos.model.TransaccionPago;
import cl.duoc.jv0101.foodgo.pagos.exception.BusinessRuleException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.duoc.jv0101.foodgo.pagos.model.Pago;
import cl.duoc.jv0101.foodgo.pagos.repository.PagoRepository;

@Service
@Transactional
public class PagoService {

    private final PagoRepository repository;

    public PagoService(PagoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Pago> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Pago> findById(Long id) {
        return repository.findById(id);
    }

    public Pago create(Pago recurso) {
        recurso.setId(null);
        recurso.getTransacciones().forEach(item -> item.setId(null));
        validarTransacciones(recurso, recurso.getTransacciones());
        return repository.save(recurso);
    }

    public Optional<Pago> update(Long id, Pago datos) {
        return repository.findById(id).map(existente -> {
            existente.setPedido(datos.getPedido());
            existente.setMetodo(datos.getMetodo());
            existente.setMonto(datos.getMonto());
            validarTransacciones(existente, existente.getTransacciones());
            return repository.save(existente);
        });
    }

    public boolean delete(Long id) {
        return repository.findById(id).map(existente -> {
            repository.delete(existente);
            return true;
        }).orElse(false);
    }
    public static void validarTransacciones(Pago pago, List<TransaccionPago> transacciones) {
        BigDecimal cobros = BigDecimal.ZERO;
        BigDecimal reembolsos = BigDecimal.ZERO;
        for (TransaccionPago transaccion : transacciones) {
            if (transaccion.getMonto().compareTo(pago.getMonto()) > 0) {
                throw new BusinessRuleException("monto", "Una transacción no puede superar el monto del pago");
            }
            if ("APROBADA".equals(transaccion.getEstado())) {
                if ("COBRO".equals(transaccion.getTipo())) cobros = cobros.add(transaccion.getMonto());
                else reembolsos = reembolsos.add(transaccion.getMonto());
            }
        }
        if (cobros.compareTo(pago.getMonto()) > 0) {
            throw new BusinessRuleException("monto", "Los cobros aprobados no pueden superar el monto del pago");
        }
        if (reembolsos.compareTo(cobros) > 0) {
            throw new BusinessRuleException("monto", "No se puede reembolsar más de lo cobrado y aprobado");
        }
    }
}
