package com.study.spring6restmvc.repositories;

import com.study.spring6restmvc.bootstrap.BootstrapData;
import com.study.spring6restmvc.entities.Beer;
import com.study.spring6restmvc.entities.BeerOrder;
import com.study.spring6restmvc.entities.BeerOrderLine;
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

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvService.class, CustomerCsvService.class})
class BeerOrderRepositoryTest {

    @Autowired
    private BeerOrderRepository beerOrderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private BeerRepository beerRepository;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.findAll().getFirst();
    }

    @Transactional
    @Test
    void testBeerOrders() {
        var beerIterator = beerRepository.findAll().iterator();
        Beer beer1 = beerIterator.next();
        Beer beer2 = beerIterator.next();
        var beerOrderLine1 = BeerOrderLine.builder()
                .beer(beer1)
                .orderQuantity(1)
                .build();
        var beerOrderLine2 = BeerOrderLine.builder()
                .beer(beer2)
                .orderQuantity(2)
                .build();

        var beerOrder = BeerOrder.builder()
                .customerRef("Test Order")
                .customer(testCustomer)
                .beerOrderLines(Set.of(beerOrderLine1, beerOrderLine2))
                .beerOrderShipment(BeerOrderShipment.builder()
                        .trackingNumber("12345test")
                        .build())
                .build();

        beerOrder = beerOrderRepository.save(beerOrder);
        testCustomer = customerRepository.findAll().getFirst();

        assertThat(beerOrder.getCustomer()).isEqualTo(testCustomer);
        assertThat(testCustomer.getBeerOrders()).contains(beerOrder);
        assertThat(beerOrder.getBeerOrderShipment().getBeerOrder()).isEqualTo(beerOrder);
        assertThat(beerOrder.getBeerOrderLines()).contains(beerOrderLine1, beerOrderLine2);
        assertThat(beerOrder.getBeerOrderLines()).allSatisfy(beerOrderLine -> assertThat(beerOrderLine.getId()).isNotNull());
    }
}
