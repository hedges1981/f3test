package form3test.repositories;

/**
 * Created by rhall on 12/01/2018.
 */

import form3test.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PaymentRepository extends JpaRepository<Payment, String> {
}
