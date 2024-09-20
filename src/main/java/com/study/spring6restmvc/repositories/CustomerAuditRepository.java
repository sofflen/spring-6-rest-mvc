package com.study.spring6restmvc.repositories;

import com.study.spring6restmvc.entities.CustomerAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerAuditRepository extends JpaRepository<CustomerAudit, UUID> {
}
