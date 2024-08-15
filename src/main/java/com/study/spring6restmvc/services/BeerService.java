package com.study.spring6restmvc.services;

import com.study.spring6restmvc.model.Beer;

import java.util.List;
import java.util.UUID;

public interface BeerService {

    Beer getBeerById(UUID uuid);

    List<Beer> getAllBeers();

    Beer saveBeer(Beer beer);

    Beer updateBeerById(UUID beerId, Beer beer);
}
