package com.study.spring6restmvc.services;

import com.study.spring6restmvc.model.Beer;
import com.study.spring6restmvc.model.BeerStyle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class BeerServiceImpl implements BeerService {

    private final Map<UUID, Beer> beersMap = new HashMap<>();

    BeerServiceImpl() {
        Beer beer1 = Beer.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerName("Galaxy Cat")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(122)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        Beer beer2 = Beer.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerName("Crank")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356222")
                .price(new BigDecimal("11.99"))
                .quantityOnHand(392)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        Beer beer3 = Beer.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerName("Sunshine City")
                .beerStyle(BeerStyle.IPA)
                .upc("12356")
                .price(new BigDecimal("13.99"))
                .quantityOnHand(144)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        beersMap.put(beer1.getId(), beer1);
        beersMap.put(beer2.getId(), beer2);
        beersMap.put(beer3.getId(), beer3);
    }

    @Override
    public List<Beer> getAllBeers() {
        return new ArrayList<>(beersMap.values());
    }

    @Override
    public Beer saveBeer(Beer beer) {
        Beer savedBeer = Beer.builder()
                .id(UUID.randomUUID())
                .version(1)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .upc(beer.getUpc())
                .price(beer.getPrice())
                .quantityOnHand(beer.getQuantityOnHand())
                .beerStyle(beer.getBeerStyle())
                .beerName(beer.getBeerName())
                .build();

        beersMap.put(savedBeer.getId(), savedBeer);
        log.info("BeerService: Saved Beer: {}", savedBeer);
        return savedBeer;
    }

    @Override
    public Beer updateBeerById(UUID beerId, Beer beer) {
        Beer existingBeer = beersMap.get(beerId);
        log.info("BeerService: Updating Beer: {}\nBeer before update: {}", beerId, existingBeer);

        existingBeer.setUpc(beer.getUpc());
        existingBeer.setPrice(beer.getPrice());
        existingBeer.setQuantityOnHand(beer.getQuantityOnHand());
        existingBeer.setModifiedAt(LocalDateTime.now());
        existingBeer.setBeerName(beer.getBeerName());

        log.info("Beer after update: {}", existingBeer);
        return existingBeer;
    }

    @Override
    public Beer getBeerById(UUID id) {
        log.info("BeerService: getBeerById({})", id);

        return beersMap.get(id);
    }
}
