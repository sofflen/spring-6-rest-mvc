package com.study.spring6restmvc.controllers;

import com.study.spring6restmvc.exceptions.NotFoundException;
import com.study.spring6restmvc.repositories.BeerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BeerControllerIntegrationTest {

    @Autowired
    private BeerController beerController;
    @Autowired
    private BeerRepository beerRepository;

    @Test
    void testGetAllBeers() {
        var beerDtoList = beerController.getAllBeers();

        assertThat(beerDtoList).isNotNull();
        assertThat(beerDtoList.size()).isEqualTo(3);
    }

    @Test
    @Transactional
    void testGetAllBeersReturnsEmptyListIfNoBeers() {
        beerRepository.deleteAll();

        var beerDtoList = beerController.getAllBeers();

        assertThat(beerDtoList).isNotNull();
        assertThat(beerDtoList.isEmpty()).isTrue();
    }

    @Test
    void getBeerById() {
        var beer = beerRepository.findAll().getFirst();
        var beerDto = beerController.getBeerById(beer.getId());

        assertThat(beerDto).isNotNull();
    }

    @Test
    void getBeerByIdThrowsNotFoundExceptionIfBeerDoesNotExist() {
        assertThrows(NotFoundException.class,
                () -> beerController.getBeerById(UUID.randomUUID()));
    }
}
