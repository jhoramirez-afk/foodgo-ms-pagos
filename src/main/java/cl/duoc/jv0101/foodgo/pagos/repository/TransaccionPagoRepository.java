package cl.duoc.jv0101.foodgo.pagos.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.jv0101.foodgo.pagos.model.TransaccionPago;

public interface TransaccionPagoRepository extends JpaRepository<TransaccionPago, Long> {
    List<TransaccionPago> findByPago_Id(Long pagoId);
}
