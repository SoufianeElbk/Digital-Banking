package ma.enset.digitalbanking.repositories;

import ma.enset.digitalbanking.entities.AccountOperation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOperationRepsitory extends JpaRepository<AccountOperation, Long> {

}
