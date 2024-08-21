package com.study.spring6restmvc.services;

import com.study.spring6restmvc.model.BeerCsvRecord;
import com.study.spring6restmvc.model.CustomerCsvRecord;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class CsvServiceTest {

    CsvService<BeerCsvRecord> beerCsvService = new BeerCsvService();
    CsvService<CustomerCsvRecord> customerCsvService = new CustomerCsvService();

    File csvFile;

    @Test
    void testConvertBeerCsvToList() throws FileNotFoundException {
        csvFile = ResourceUtils.getFile("classpath:csvdata/beers.csv");
        var beerCsvRecordList = beerCsvService.convertCSVToList(csvFile);

        assertThat(beerCsvRecordList).isNotNull();
        assertThat(beerCsvRecordList.size()).isEqualTo(2410);
    }

    @Test
    void testConvertCustomerCsvToList() throws FileNotFoundException {
        csvFile = ResourceUtils.getFile("classpath:csvdata/customers.csv");
        var customerCsvRecordList = customerCsvService.convertCSVToList(csvFile);

        assertThat(customerCsvRecordList).isNotNull();
        assertThat(customerCsvRecordList.size()).isEqualTo(2000);
    }
}