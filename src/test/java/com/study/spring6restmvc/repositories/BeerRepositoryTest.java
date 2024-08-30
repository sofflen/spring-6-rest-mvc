package com.study.spring6restmvc.repositories;

import com.study.spring6restmvc.bootstrap.BootstrapData;
import com.study.spring6restmvc.entities.Beer;
import com.study.spring6restmvc.model.BeerStyle;
import com.study.spring6restmvc.services.BeerCsvService;
import com.study.spring6restmvc.services.CustomerCsvService;
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
@Import({BootstrapData.class, BeerCsvService.class, CustomerCsvService.class})
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
        String tooLongName = "beer 01234567890123456789012345678901234567890123456789";
        var testBeer = Beer.builder()
                .beerName(tooLongName)
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("123123123")
                .price(new BigDecimal("11.99"))
                .build();

        assertThrows(ConstraintViolationException.class,
                () -> beerRepository.saveAndFlush(testBeer));
    }

    @Test
    void getBeersByBeerNameIsLikeIgnoreCase() {
        var beerPage = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%IPA%", null);

        assertThat(beerPage.getContent()).hasSizeGreaterThan(300);
    }

    @Test
    void getBeersByBeerStyle() {
        var beerPage = beerRepository.findAllByBeerStyle(BeerStyle.PALE_ALE, null);

        assertThat(beerPage.getContent()).hasSizeGreaterThan(10);
    }

    @Test
    void getBeersByBeerNameAndBeerStyle() {
        var beerPage = beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle("%india%", BeerStyle.IPA, null);

        assertThat(beerPage.getContent()).hasSizeGreaterThan(40);
    }
}
