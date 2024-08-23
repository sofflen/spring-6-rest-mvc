package com.study.spring6restmvc.repositories;

import com.study.spring6restmvc.bootstrap.BootstrapData;
import com.study.spring6restmvc.entities.Customer;
import com.study.spring6restmvc.services.BeerCsvService;
import com.study.spring6restmvc.services.CustomerCsvService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvService.class, CustomerCsvService.class})
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

    @Test
    void getCustomersByCustomerNameIsLikeIgnoreCase() {
        var customerPage = customerRepository.findAllByCustomerNameIsLikeIgnoreCase("%john%", null);

        assertThat(customerPage.getContent().size()).isGreaterThan(10);
    }

    @Test
    void getBeersByBeerStyle() {
        var customerPage = customerRepository.findAllByEmail("john.doe@gmail.com", null);

        assertThat(customerPage.getContent().size()).isEqualTo(1);
    }

    @Test
    void getBeersByBeerNameAndBeerStyle() {
        var customerPage = customerRepository
                .findAllByCustomerNameIsLikeIgnoreCaseAndEmail("%john%", "john.doe@gmail.com", null);

        assertThat(customerPage.getContent().size()).isEqualTo(1);
    }
}
