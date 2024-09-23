package com.study.spring6restmvc.bootstrap;

import com.study.spring6restmvc.model.BeerCsvRecord;
import com.study.spring6restmvc.model.CustomerCsvRecord;
import com.study.spring6restmvc.repositories.BeerOrderRepository;
import com.study.spring6restmvc.repositories.BeerRepository;
import com.study.spring6restmvc.repositories.CustomerRepository;
import com.study.spring6restmvc.services.BeerCsvService;
import com.study.spring6restmvc.services.CsvService;
import com.study.spring6restmvc.services.CustomerCsvService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({BeerCsvService.class, CustomerCsvService.class})
class BootstrapDataTest {

    @Autowired
    private BeerRepository beerRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private BeerOrderRepository beerOrderRepository;
    @Autowired
    private CsvService<BeerCsvRecord> beerCsvService;
    @Autowired
    CsvService<CustomerCsvRecord> customerCsvService;

    private BootstrapData bootstrapData;

    @BeforeEach
    void setUp() {
        bootstrapData = new BootstrapData(beerRepository, customerRepository, beerOrderRepository, beerCsvService, customerCsvService);
    }

    @Test
    void testRun() throws Exception {
        bootstrapData.run(null);

        assertThat(beerRepository.count()).isEqualTo(2413);
        assertThat(customerRepository.count()).isEqualTo(2003);
    }
}
