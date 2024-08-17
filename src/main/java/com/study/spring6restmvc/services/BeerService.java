package com.study.spring6restmvc.services;

import com.study.spring6restmvc.model.Beer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    Optional<Beer> getBeerById(UUID uuid);

    List<Beer> getAllBeers();

    Beer saveBeer(Beer beer);

    void updateBeerById(UUID beerId, Beer beer);

    void deleteBeerById(UUID beerId);

    void patchBeerById(UUID beerId, Beer beer);
}
