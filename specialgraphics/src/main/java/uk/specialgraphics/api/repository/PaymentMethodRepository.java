package uk.specialgraphics.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.specialgraphics.api.entity.GupType;
import uk.specialgraphics.api.entity.PaymentMethod;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {
    PaymentMethod getPaymentMethodById(Integer paymentMethodId);
}
