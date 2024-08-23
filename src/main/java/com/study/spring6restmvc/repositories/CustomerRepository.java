package com.study.spring6restmvc.repositories;

import com.study.spring6restmvc.entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Page<Customer> findAllByCustomerNameIsLikeIgnoreCase(String customerName, Pageable pageable);

    Page<Customer> findAllByEmail(String email, Pageable pageable);

    Page<Customer> findAllByCustomerNameIsLikeIgnoreCaseAndEmail(String customerName, String email, Pageable pageable);
}
