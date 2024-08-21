package com.study.spring6restmvc.services;

import com.study.spring6restmvc.model.BeerCSVRecord;

import java.io.File;
import java.util.List;

public interface BeerCsvService {

    List<BeerCSVRecord> convertCSVToList(File csvFile);
}
