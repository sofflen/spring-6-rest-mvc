package com.study.spring6restmvc.controllers;

import com.study.spring6restmvc.model.Beer;
import com.study.spring6restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/beer")
public class BeerController {

    private final BeerService beerService;

    @GetMapping
    public List<Beer> getAllBeers() {
        log.info("BeerController: getAllBeers()");
        return beerService.getAllBeers();
    }

    @GetMapping("/{beerId}")
    public Beer getBeerById(@PathVariable("beerId") UUID beerId) {
        log.info("BeerController: getBeerById({})", beerId);

        return beerService.getBeerById(beerId);
    }
}
