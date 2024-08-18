package com.study.spring6restmvc.controllers;

import com.study.spring6restmvc.entities.Beer;
import com.study.spring6restmvc.exceptions.NotFoundException;
import com.study.spring6restmvc.mappers.BeerMapper;
import com.study.spring6restmvc.model.BeerDTO;
import com.study.spring6restmvc.repositories.BeerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
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
    @Autowired
    private BeerMapper beerMapper;

    private Beer testBeer;

    @BeforeEach
    void setUp() {
        testBeer = beerRepository.findAll().getFirst();
    }

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
    void testGetBeerById() {
        var testBeer = beerRepository.findAll().getFirst();
        var beerDto = beerController.getBeerById(testBeer.getId());

        assertThat(beerDto).isNotNull();
    }

    @Test
    void testGetBeerByIdThrowsNotFoundExceptionIfBeerDoesNotExist() {
        assertThrows(NotFoundException.class,
                () -> beerController.getBeerById(UUID.randomUUID()));
    }

    @Test
    @Transactional
    void testCreateBeer() {
        var beerDto = BeerDTO.builder()
                .beerName("New Beer")
                .build();

        var responseEntity = beerController.createBeer(beerDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        var locationSplitStrArray = responseEntity.getHeaders().getLocation().toString().split("/");
        var beerId = UUID.fromString(locationSplitStrArray[locationSplitStrArray.length - 1]);

        assertThat(beerRepository.findById(beerId)).isNotNull();
    }

    @Test
    void testUpdateBeerThrowsNotFoundExceptionIfBeerDoesNotExist() {
        assertThrows(NotFoundException.class,
                () -> beerController.updateBeerById(UUID.randomUUID(), BeerDTO.builder().build()));
    }

    @Test
    @Transactional
    void testUpdateBeer() {
        var testBeer = beerRepository.findAll().getFirst();
        var beerDto = beerMapper.beerToBeerDTO(testBeer);
        final String newBeerName = "New Beer";

        beerDto.setId(null);
        beerDto.setVersion(null);
        beerDto.setBeerName(newBeerName);

        var responseEntity = beerController.updateBeerById(testBeer.getId(), beerDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        var updatedBeer = beerRepository.findById(testBeer.getId()).orElseThrow();

        assertThat(updatedBeer.getBeerName()).isEqualTo(newBeerName);
    }

    @Test
    @Transactional
    void testDeleteBeerById() {
        var beerId = testBeer.getId();

        var ResponseEntity = beerController.deleteBeerById(beerId);

        assertThat(ResponseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(beerRepository.findById(beerId).isEmpty()).isTrue();
    }

    @Test
    void testDeleteByIdThrowsNotFoundExceptionIfBeerDoesNotExist() {
        assertThrows(NotFoundException.class,
                () -> beerController.deleteBeerById(UUID.randomUUID()));
    }

    @Test
    @Transactional
    void testPatchBeerById() {
        var testBeer = beerRepository.findAll().getFirst();
        var beerDto = beerMapper.beerToBeerDTO(testBeer);
        final String newBeerName = "New Beer";

        beerDto.setId(null);
        beerDto.setVersion(null);
        beerDto.setBeerName(newBeerName);

        var responseEntity = beerController.patchBeerById(testBeer.getId(), beerDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        var updatedBeer = beerRepository.findById(testBeer.getId()).orElseThrow();

        assertThat(updatedBeer.getBeerName()).isEqualTo(newBeerName);
    }
}
