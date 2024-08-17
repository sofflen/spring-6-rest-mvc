package com.study.spring6restmvc.controllers;

import com.study.spring6restmvc.model.Beer;
import com.study.spring6restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BeerController {

    private final BeerService beerService;

    public static final String BEER_PATH = "/api/v1/beer";
    public static final String BEER_PATH_ID = "/api/v1/beer/{id}";

    @GetMapping(BEER_PATH)
    public List<Beer> getAllBeers() {
        log.info("BeerController: getAllBeers()");
        return beerService.getAllBeers();
    }

    @GetMapping(BEER_PATH_ID)
    public Beer getBeerById(@PathVariable("id") UUID beerId) {
        log.info("BeerController: getBeerById({})", beerId);

        return beerService.getBeerById(beerId);
    }

    @PostMapping(BEER_PATH)
    public ResponseEntity<Beer> createBeer(@RequestBody Beer beer) {
        log.info("BeerController: createBeer({})", beer);

        Beer savedBeer = beerService.saveBeer(beer);
        HttpHeaders headers = new HttpHeaders();

        headers.add("Location", BEER_PATH + "/" + savedBeer.getId());

        return new ResponseEntity<>(savedBeer, headers, HttpStatus.CREATED);
    }

    @PutMapping(BEER_PATH_ID)
    public ResponseEntity<Beer> updateBeerById(@PathVariable("id") UUID beerId, @RequestBody Beer beer) {
        log.info("BeerController: updateBeerById({})", beerId);

        beerService.updateBeerById(beerId, beer);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(BEER_PATH_ID)
    public ResponseEntity<Beer> deleteBeerById(@PathVariable("id") UUID beerId) {
        log.info("BeerController: deleteBeerById({})", beerId);

        beerService.deleteBeerById(beerId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(BEER_PATH_ID)
    public ResponseEntity<Beer> patchBeerById(@PathVariable("id") UUID beerId, @RequestBody Beer beer) {
        log.info("BeerController: patchBeerById({})", beerId);

        beerService.patchBeerById(beerId, beer);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
