package com.study.spring6restmvc.services;

import java.io.File;
import java.util.List;

public interface CsvService<T> {

    List<T> convertCSVToList(File csvFile);
}
