package cl.duoc.jv0101.foodgo.pagos.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.jv0101.foodgo.pagos.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    @Override
    @EntityGraph(attributePaths = "transacciones")
    List<Pago> findAll();

    @Override
    @EntityGraph(attributePaths = "transacciones")
    Optional<Pago> findById(Long id);
}
