package com.study.spring6restmvc.services;

import org.springframework.data.domain.PageRequest;

import java.util.Objects;

public class ServiceUtils {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private ServiceUtils() {
    }

    public static PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        int queryPageNumber;
        int queryPageSize;

        if (pageNumber == null || pageNumber == 0) {
            queryPageNumber = DEFAULT_PAGE_NUMBER;
        } else {
            queryPageNumber = pageNumber - 1;
        }

        if (pageSize != null && pageSize > 1000) {
            queryPageSize = 1000;
        } else {
            queryPageSize = Objects.requireNonNullElse(pageSize, DEFAULT_PAGE_SIZE);
        }

        return PageRequest.of(queryPageNumber, queryPageSize);
    }
}
