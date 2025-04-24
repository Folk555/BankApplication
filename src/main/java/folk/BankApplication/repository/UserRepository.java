package folk.BankApplication.repository;

import folk.BankApplication.model.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    boolean existsByName(String name);

    default Page<User> findWithFilters(String name, String email, String phone, LocalDate dateOfBirth, Pageable pageable) {
        return findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null) {
                predicates.add(cb.like(root.get("name"), name + "%"));
            }

            if (dateOfBirth != null) {
                predicates.add(cb.greaterThan(root.get("dateOfBirth"), dateOfBirth));
            }

            if (email != null) {
                predicates.add(cb.equal(root.join("emails").get("email"), email));
            }

            if (phone != null) {
                predicates.add(cb.equal(root.join("phones").get("phone"), phone));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
}
