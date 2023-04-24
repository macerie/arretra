package com.example.application.data.service;

import com.example.application.data.entity.Sedi;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SediService {

    private final SediRepository repository;

    public SediService(SediRepository repository) {
        this.repository = repository;
    }

    public Optional<Sedi> get(Long id) {
        return repository.findById(id);
    }

    public Sedi update(Sedi entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Sedi> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Sedi> list(Pageable pageable, Specification<Sedi> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
