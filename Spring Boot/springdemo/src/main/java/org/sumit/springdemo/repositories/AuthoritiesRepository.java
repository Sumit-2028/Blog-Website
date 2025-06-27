package org.sumit.springdemo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.sumit.springdemo.model.Authority;

@Repository
public interface AuthoritiesRepository extends JpaRepository<Authority, Long> {

    Authority save(Authority authority);
    
}
