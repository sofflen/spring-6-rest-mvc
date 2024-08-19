package com.study.spring6restmvc.controllers;

import com.study.spring6restmvc.entities.Customer;
import com.study.spring6restmvc.exceptions.NotFoundException;
import com.study.spring6restmvc.mappers.CustomerMapper;
import com.study.spring6restmvc.model.CustomerDTO;
import com.study.spring6restmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
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
    @Autowired
    private CustomerMapper customerMapper;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.findAll().getFirst();
    }

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

    @Test
    @Transactional
    void testCreateCustomer() {
        var customerDto = CustomerDTO.builder()
                .customerName("New Customer")
                .build();

        var responseEntity = customerController.createCustomer(customerDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        var locationSplitStrArray = responseEntity.getHeaders().getLocation().toString().split("/");
        var customerId = UUID.fromString(locationSplitStrArray[locationSplitStrArray.length - 1]);

        assertThat(customerRepository.findById(customerId)).isNotNull();
    }

    @Test
    void testUpdateCustomerThrowsNotFoundExceptionIfCustomerDoesNotExist() {
        assertThrows(NotFoundException.class,
                () -> customerController.updateCustomerById(UUID.randomUUID(), CustomerDTO.builder().build()));
    }

    @Test
    @Transactional
    void testUpdateCustomer() {
        var testCustomer = customerRepository.findAll().getFirst();
        var customerDto = customerMapper.customerToCustomerDTO(testCustomer);
        final String newCustomerName = "New Customer";

        customerDto.setId(null);
        customerDto.setVersion(null);
        customerDto.setCustomerName(newCustomerName);

        var responseEntity = customerController.updateCustomerById(testCustomer.getId(), customerDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        var updatedCustomer = customerRepository.findById(testCustomer.getId()).orElseThrow();

        assertThat(updatedCustomer.getCustomerName()).isEqualTo(newCustomerName);
    }

    @Test
    @Transactional
    void testDeleteCustomerById() {
        var customerId = testCustomer.getId();

        var ResponseEntity = customerController.deleteCustomerById(customerId);

        assertThat(ResponseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(customerRepository.findById(customerId).isEmpty()).isTrue();
    }

    @Test
    void testDeleteByIdThrowsNotFoundExceptionIfCustomerDoesNotExist() {
        assertThrows(NotFoundException.class,
                () -> customerController.deleteCustomerById(UUID.randomUUID()));
    }

    @Test
    @Transactional
    void testPatchCustomerById() {
        var testCustomer = customerRepository.findAll().getFirst();
        var customerDto = customerMapper.customerToCustomerDTO(testCustomer);
        final String newCustomerName = "New Customer";

        customerDto.setId(null);
        customerDto.setVersion(null);
        customerDto.setCustomerName(newCustomerName);

        var responseEntity = customerController.patchCustomerById(testCustomer.getId(), customerDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        var updatedCustomer = customerRepository.findById(testCustomer.getId()).orElseThrow();

        assertThat(updatedCustomer.getCustomerName()).isEqualTo(newCustomerName);
    }
}
