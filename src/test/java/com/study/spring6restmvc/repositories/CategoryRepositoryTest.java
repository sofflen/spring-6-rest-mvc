package com.study.spring6restmvc.repositories;

import com.study.spring6restmvc.bootstrap.BootstrapData;
import com.study.spring6restmvc.entities.Beer;
import com.study.spring6restmvc.entities.Category;
import com.study.spring6restmvc.services.BeerCsvService;
import com.study.spring6restmvc.services.CustomerCsvService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvService.class, CustomerCsvService.class})
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BeerRepository beerRepository;

    private Beer testBeer;

    @BeforeEach
    void setUp() {
        testBeer = beerRepository.findAll().getFirst();
    }

    @Transactional
    @Test
    void testAddCategory() {
        var category = Category.builder()
                .description("Test Category")
                .build();
        category.addBeer(testBeer);
        category = categoryRepository.save(category);

        testBeer = beerRepository.findAll().getFirst();

        assertThat(category.getBeers()).contains(testBeer);
        assertThat(testBeer.getCategories()).contains(category);
    }
}
