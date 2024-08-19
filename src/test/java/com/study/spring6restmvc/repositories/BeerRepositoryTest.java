package com.study.spring6restmvc.repositories;

import com.study.spring6restmvc.entities.Beer;
import com.study.spring6restmvc.model.BeerStyle;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
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
}
