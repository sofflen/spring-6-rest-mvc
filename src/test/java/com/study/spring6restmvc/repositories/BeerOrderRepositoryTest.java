package com.study.spring6restmvc.repositories;

import com.study.spring6restmvc.bootstrap.BootstrapData;
import com.study.spring6restmvc.entities.BeerOrder;
import com.study.spring6restmvc.entities.BeerOrderShipment;
import com.study.spring6restmvc.entities.Customer;
import com.study.spring6restmvc.services.BeerCsvService;
import com.study.spring6restmvc.services.CustomerCsvService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvService.class, CustomerCsvService.class})
class BeerOrderRepositoryTest {

    @Autowired
    private BeerOrderRepository beerOrderRepository;
    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.findAll().getFirst();
    }

    @Transactional
    @Test
    void testBeerOrders() {
        var beerOrder = BeerOrder.builder()
                .customerRef("Test Order")
                .customer(testCustomer)
                .beerOrderShipment(BeerOrderShipment.builder()
                        .trackingNumber("12345test")
                        .build())
                .build();

        beerOrder = beerOrderRepository.save(beerOrder);
        testCustomer = customerRepository.findAll().getFirst();

        assertThat(beerOrder.getCustomer()).isEqualTo(testCustomer);
        assertThat(testCustomer.getBeerOrders()).contains(beerOrder);
        assertThat(beerOrder.getBeerOrderShipment().getBeerOrder()).isEqualTo(beerOrder);
    }
}
