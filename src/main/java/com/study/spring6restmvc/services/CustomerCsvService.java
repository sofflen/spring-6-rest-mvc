package com.study.spring6restmvc.services;

import com.opencsv.bean.CsvToBeanBuilder;
import com.study.spring6restmvc.model.CustomerCsvRecord;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

@Service
public class CustomerCsvService implements CsvService<CustomerCsvRecord> {
    @Override
    public List<CustomerCsvRecord> convertCSVToList(File csvFile) {
        try {
            return new CsvToBeanBuilder<CustomerCsvRecord>(new FileReader(csvFile))
                    .withType(CustomerCsvRecord.class)
                    .build().parse();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
