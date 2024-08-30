package com.study.spring6restmvc.services;

import com.opencsv.bean.CsvToBeanBuilder;
import com.study.spring6restmvc.model.BeerCsvRecord;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

@Service
public class BeerCsvService implements CsvService<BeerCsvRecord> {
    @Override
    public List<BeerCsvRecord> convertCSVToList(File csvFile) throws FileNotFoundException {
        try {
            return new CsvToBeanBuilder<BeerCsvRecord>(new FileReader(csvFile))
                    .withType(BeerCsvRecord.class)
                    .build().parse();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Missing .csv file with beer records");
        }
    }
}
