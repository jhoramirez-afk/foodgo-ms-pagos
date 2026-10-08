package cl.duoc.jv0101.foodgo.pagos.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.duoc.jv0101.foodgo.pagos.exception.ResourceNotFoundException;
import cl.duoc.jv0101.foodgo.pagos.model.TransaccionPago;
import cl.duoc.jv0101.foodgo.pagos.model.Pago;
import cl.duoc.jv0101.foodgo.pagos.repository.TransaccionPagoRepository;
import cl.duoc.jv0101.foodgo.pagos.repository.PagoRepository;

@Service
@Transactional
public class TransaccionPagoService {

    private final TransaccionPagoRepository repository;
    private final PagoRepository pagoRepository;

    public TransaccionPagoService(TransaccionPagoRepository repository, PagoRepository pagoRepository) {
        this.repository = repository;
        this.pagoRepository = pagoRepository;
    }

    @Transactional(readOnly = true)
    public List<TransaccionPago> findByPagoId(Long pagoId) {
        if (!pagoRepository.existsById(pagoId)) {
            throw new ResourceNotFoundException("Pago no encontrado con id " + pagoId);
        }
        return repository.findByPago_Id(pagoId);
    }

    @Transactional(readOnly = true)
    public TransaccionPago findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransaccionPago no encontrado con id " + id));
    }

    public TransaccionPago create(Long pagoId, TransaccionPago recurso) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id " + pagoId));
        recurso.setId(null);
        recurso.setPago(pago);
        return repository.save(recurso);
    }

    public TransaccionPago update(Long id, TransaccionPago datos) {
        TransaccionPago existente = findById(id);
        existente.setTipo(datos.getTipo());
        existente.setMonto(datos.getMonto());
        existente.setEstado(datos.getEstado());
        existente.setReferencia(datos.getReferencia());
        return repository.save(existente);
    }

    public void delete(Long id) {
        TransaccionPago existente = findById(id);
        repository.delete(existente);
    }
}
