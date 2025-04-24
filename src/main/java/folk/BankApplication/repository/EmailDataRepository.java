package folk.BankApplication.repository;

import folk.BankApplication.model.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailDataRepository extends JpaRepository<EmailData, Long> {

    Optional<EmailData> findByEmail(String email);
    boolean existsByEmail(String email);
}