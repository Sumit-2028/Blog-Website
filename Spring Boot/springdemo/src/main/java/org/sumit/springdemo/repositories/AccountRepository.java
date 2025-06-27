package org.sumit.springdemo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // ✅ You missed this import
import org.sumit.springdemo.model.Account;

@Repository // ✅ Add this annotation
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String username);

    Account findByEmailAndPassword(String email, String password);

    Optional<Account> findById(Long id);

    Optional<Account> findByPasswordResetToken(String password_reset_token);

    

}
