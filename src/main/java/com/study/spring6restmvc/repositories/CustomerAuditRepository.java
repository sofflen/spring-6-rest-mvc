package com.study.spring6restmvc.repositories;

import com.study.spring6restmvc.entities.CustomerAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerAuditRepository extends JpaRepository<CustomerAudit, UUID> {
}
