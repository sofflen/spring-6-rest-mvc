package com.study.spring6restmvc.repositories;

import com.study.spring6restmvc.entities.BeerOrderShipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BeerOrderShipmentRepository extends JpaRepository<BeerOrderShipment, UUID> {
}
