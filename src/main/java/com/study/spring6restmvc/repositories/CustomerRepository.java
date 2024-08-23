package com.study.spring6restmvc.repositories;

import com.study.spring6restmvc.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    List<Customer> findAllByCustomerNameIsLikeIgnoreCase(String customerName);

    List<Customer> findAllByEmail(String email);

    List<Customer> findAllByCustomerNameIsLikeIgnoreCaseAndEmail(String customerName, String email);
}
