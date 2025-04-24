package folk.BankApplication.repository;

import folk.BankApplication.model.PhoneData;
import folk.BankApplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {

    Optional<PhoneData> findByPhone(String phone);

    boolean existsByPhone(String phone);

    // Для аутентификации по телефону
    @Query("SELECT p.user FROM PhoneData p WHERE p.phone = :phone")
    Optional<User> findUserByPhone(@Param("phone") String phone);
}
