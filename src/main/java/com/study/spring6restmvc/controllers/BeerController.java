package com.study.spring6restmvc.controllers;

import com.study.spring6restmvc.model.Beer;
import com.study.spring6restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BeerController {

    private final BeerService beerService;

    @RequestMapping("api/v1/beer")
    public List<Beer> getAllBeers() {
        log.info("BeerController: getAllBeers()");
        return beerService.getAllBeers();
    }

    public Beer getBeerById(UUID id) {
        log.info("getBeerById - Controller");

        return beerService.getBeerById(id);
    }
}
