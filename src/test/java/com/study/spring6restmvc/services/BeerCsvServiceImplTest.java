package com.study.spring6restmvc.services;

import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BeerCsvServiceImplTest {

    BeerCsvService service = new BeerCsvServiceImpl();

    @Test
    void testConvertCSV() throws FileNotFoundException {
        File csvFile = ResourceUtils.getFile("classpath:csvdata/beers.csv");
        var beerCsvRecordList = service.convertCSV(csvFile);

        assertThat(beerCsvRecordList).isNotNull();
        assertThat(beerCsvRecordList.size()).isGreaterThan(0);
    }
}