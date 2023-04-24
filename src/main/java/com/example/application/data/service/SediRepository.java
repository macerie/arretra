package com.example.application.data.service;

import com.example.application.data.entity.Sedi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SediRepository extends JpaRepository<Sedi, Long>, JpaSpecificationExecutor<Sedi> {

}
