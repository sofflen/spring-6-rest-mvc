package com.study.spring6restmvc.services;

import com.study.spring6restmvc.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    Optional<Customer> getCustomerById(UUID id);

    List<Customer> getAllCustomers();

    Customer saveCustomer(Customer customer);

    void updateCustomerById(UUID id, Customer customer);

    void deleteCustomerById(UUID customerId);

    void patchCustomerById(UUID customerId, Customer customer);
}
