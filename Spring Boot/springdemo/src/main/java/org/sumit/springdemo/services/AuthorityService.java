package org.sumit.springdemo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sumit.springdemo.model.Authority;
import org.sumit.springdemo.repositories.AuthoritiesRepository;

@Service
public class AuthorityService {

    private final AuthoritiesRepository authoritiesRepository;

    @Autowired
    public AuthorityService(AuthoritiesRepository authoritiesRepository) {
        this.authoritiesRepository = authoritiesRepository;
    }

    public Authority save(Authority authority) {
        return authoritiesRepository.save(authority);
    }

    public Optional<Authority> findById(Long id) {
        return authoritiesRepository.findById(id);
    }

    public List<Authority> findAll() {
        return authoritiesRepository.findAll();
    }

    public void deleteById(Long id) {
        authoritiesRepository.deleteById(id);
    }
}
