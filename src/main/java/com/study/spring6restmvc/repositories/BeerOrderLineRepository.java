package com.study.spring6restmvc.repositories;

import com.study.spring6restmvc.entities.BeerOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BeerOrderLineRepository extends JpaRepository<BeerOrderLine, UUID> {
}
