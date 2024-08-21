package com.study.spring6restmvc.repositories;

import com.study.spring6restmvc.bootstrap.BootstrapData;
import com.study.spring6restmvc.entities.Beer;
import com.study.spring6restmvc.model.BeerStyle;
import com.study.spring6restmvc.services.BeerCsvServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class BeerRepositoryTest {

    @Autowired
    private BeerRepository beerRepository;

    @Test
    void saveBeer() {
        Beer savedBeer = beerRepository.save(Beer.builder()
                .beerName("My beer")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("123123123")
                .price(new BigDecimal("11.99"))
                .build());

        beerRepository.flush();

        assertNotNull(savedBeer);
        assertNotNull(savedBeer.getId());
    }

    @Test
    void saveBeerWithTooLongNameThrowsConstraintViolationException() {
        assertThrows(ConstraintViolationException.class, () -> {
            String tooLongName = "beer 01234567890123456789012345678901234567890123456789";
            beerRepository.save(Beer.builder()
                    .beerName(tooLongName)
                    .beerStyle(BeerStyle.PALE_ALE)
                    .upc("123123123")
                    .price(new BigDecimal("11.99"))
                    .build());

            beerRepository.flush();
        });
    }

    @Test
    void getBeersByBeerNameIsLikeIgnoreCase() {
        var beerList = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%IPA%");

        assertThat(beerList.size()).isGreaterThan(300);
    }

    @Test
    void getBeersByBeerStyle() {
        var beerList = beerRepository.findAllByBeerStyle(BeerStyle.PALE_ALE);

        assertThat(beerList.size()).isGreaterThan(10);
    }

    @Test
    void getBeersByBeerNameAndBeerStyle() {
        var beerList = beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle("%india%", BeerStyle.IPA);

        assertThat(beerList.size()).isGreaterThan(40);
    }
}
