package com.study.spring6restmvc.repositories;

import com.study.spring6restmvc.entities.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void saveCustomer() {
        Customer savedCustomer = customerRepository.save(Customer.builder()
                        .customerName("John Doe")
                .build());

        assertNotNull(savedCustomer);
        assertNotNull(savedCustomer.getId());
    }
}
