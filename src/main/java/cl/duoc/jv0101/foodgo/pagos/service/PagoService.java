package cl.duoc.jv0101.foodgo.pagos.service;

import java.util.List;
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
        return repository.save(recurso);
    }

    public Optional<Pago> update(Long id, Pago datos) {
        return repository.findById(id).map(existente -> {
            existente.setPedido(datos.getPedido());
            existente.setMetodo(datos.getMetodo());
            existente.setMonto(datos.getMonto());
            return repository.save(existente);
        });
    }

    public boolean delete(Long id) {
        return repository.findById(id).map(existente -> {
            repository.delete(existente);
            return true;
        }).orElse(false);
    }
}
