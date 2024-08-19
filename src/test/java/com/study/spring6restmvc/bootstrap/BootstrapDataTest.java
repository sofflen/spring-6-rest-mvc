package com.study.spring6restmvc.bootstrap;

import com.study.spring6restmvc.repositories.BeerRepository;
import com.study.spring6restmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BootstrapDataTest {

    @Autowired
    private BeerRepository beerRepository;
    @Autowired
    private CustomerRepository customerRepository;

    private BootstrapData bootstrapData;

    @BeforeEach
    void setUp() {
        bootstrapData = new BootstrapData(beerRepository, customerRepository);
    }

    @Test
    void testRun() throws Exception {
        bootstrapData.run(null);
        var foundBeerList = beerRepository.findAll();

        assertThat(foundBeerList).isNotNull();
        assertThat(foundBeerList).hasSize(3);
        assertThat(foundBeerList.getFirst().getId()).isNotNull();

        assertThat(customerRepository.count()).isEqualTo(3);
    }
}
