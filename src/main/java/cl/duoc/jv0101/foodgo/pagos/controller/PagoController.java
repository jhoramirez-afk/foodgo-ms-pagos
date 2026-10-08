package cl.duoc.jv0101.foodgo.pagos.controller;

import cl.duoc.jv0101.foodgo.pagos.exception.ResourceNotFoundException;
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
import cl.duoc.jv0101.foodgo.pagos.model.Pago;
import cl.duoc.jv0101.foodgo.pagos.service.PagoService;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Pago>> listar() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pago> obtener(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id " + id));
    }

    @PostMapping
    public ResponseEntity<Pago> crear(@Valid @RequestBody Pago recurso) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(recurso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pago> actualizar(@PathVariable Long id,
            @Valid @RequestBody Pago datos) {
        return service.update(id, datos).map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!service.delete(id)) {
            throw new ResourceNotFoundException("Pago no encontrado con id " + id);
        }
        return ResponseEntity.noContent().build();
    }
}
