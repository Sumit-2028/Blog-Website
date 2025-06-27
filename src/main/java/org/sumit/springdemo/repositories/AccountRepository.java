package org.sumit.springdemo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sumit.springdemo.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(String username);
    // Extends JpaRepository to inherit CRUD operations for Post entity
}