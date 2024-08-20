package com.study.spring6restmvc.repositories;

import com.study.spring6restmvc.entities.Customer;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void saveCustomer() {
        Customer savedCustomer = customerRepository.save(Customer.builder()
                .customerName("John Doe")
                .build());

        customerRepository.flush();

        assertNotNull(savedCustomer);
        assertNotNull(savedCustomer.getId());
    }

    @Test
    void saveCustomerWithTooLongNameThrowsConstraintViolationException() {
        assertThrows(ConstraintViolationException.class, () -> {
            String tooLongName = "Customer 01234567890123456789012345678901234567890123456789";
            customerRepository.save(Customer.builder()
                    .customerName(tooLongName)
                    .build());

            customerRepository.flush();
        });
    }
}
