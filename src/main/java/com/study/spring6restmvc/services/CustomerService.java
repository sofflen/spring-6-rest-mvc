package com.study.spring6restmvc.services;


import com.study.spring6restmvcapi.model.CustomerDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    Optional<CustomerDTO> getCustomerById(UUID customerId);

    Page<CustomerDTO> getAllCustomers(String customerName, String email, Integer pageNumber, Integer pageSize);

    CustomerDTO saveCustomer(CustomerDTO customer);

    Optional<CustomerDTO> updateCustomerById(UUID customerId, CustomerDTO customer);

    boolean deleteCustomerById(UUID customerId);

    Optional<CustomerDTO> patchCustomerById(UUID customerId, CustomerDTO customer);
}
