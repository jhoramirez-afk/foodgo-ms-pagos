package cl.duoc.jv0101.foodgo.pagos.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.duoc.jv0101.foodgo.pagos.model.TransaccionPago;
import cl.duoc.jv0101.foodgo.pagos.service.TransaccionPagoService;

@RestController
@RequestMapping("/api")
public class TransaccionPagoController {

    private final TransaccionPagoService service;

    public TransaccionPagoController(TransaccionPagoService service) {
        this.service = service;
    }

    @GetMapping("/pagos/{pagoId}/transacciones")
    public ResponseEntity<List<TransaccionPago>> listarPorPago(@PathVariable Long pagoId) {
        return ResponseEntity.ok(service.findByPagoId(pagoId));
    }

    @PostMapping("/pagos/{pagoId}/transacciones")
    public ResponseEntity<TransaccionPago> crear(@PathVariable Long pagoId, @Valid @RequestBody TransaccionPago recurso) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(pagoId, recurso));
    }

    @GetMapping("/transacciones/{id}")
    public ResponseEntity<TransaccionPago> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/transacciones/{id}")
    public ResponseEntity<TransaccionPago> actualizar(@PathVariable Long id, @Valid @RequestBody TransaccionPago datos) {
        return ResponseEntity.ok(service.update(id, datos));
    }

    @DeleteMapping("/transacciones/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
