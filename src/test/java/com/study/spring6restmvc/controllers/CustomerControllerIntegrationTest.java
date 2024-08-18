package com.study.spring6restmvc.controllers;

import com.study.spring6restmvc.exceptions.NotFoundException;
import com.study.spring6restmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CustomerControllerIntegrationTest {

    @Autowired
    private CustomerController customerController;
    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testGetAllCustomers() {
        var customerList = customerController.getAllCustomers();

        assertThat(customerList).isNotNull();
        assertThat(customerList.size()).isEqualTo(3);
    }

    @Test
    @Transactional
    void testGetAllCustomersReturnsEmptyListIfNoCustomers() {
        customerRepository.deleteAll();

        var customerList = customerController.getAllCustomers();

        assertThat(customerList).isNotNull();
        assertThat(customerList.isEmpty()).isTrue();
    }

    @Test
    void getCustomerById() {
        var customer = customerRepository.findAll().getFirst();
        var customerDto = customerController.getCustomerById(customer.getId());

        assertThat(customerDto).isNotNull();
    }

    @Test
    void getCustomerByIdThrowsNotFoundExceptionIfCustomerDoesNotExist() {
        assertThrows(NotFoundException.class,
                () -> customerController.getCustomerById(UUID.randomUUID()));
    }
}
