package com.study.spring6restmvc.controllers;

import com.study.spring6restmvc.model.Beer;
import com.study.spring6restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<Beer> createBeer(@RequestBody Beer beer) {
        log.info("BeerController: createBeer({})", beer);
        Beer savedBeer = beerService.saveBeer(beer);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/v1/beer/" + savedBeer.getId());
        return new ResponseEntity<>(savedBeer, headers, HttpStatus.CREATED);
    }

    @PutMapping("/{beerId}")
    public ResponseEntity<Beer> updateBeerById(@PathVariable("beerId") UUID beerId, @RequestBody Beer beer) {
        log.info("BeerController: updateBeerById({})", beerId);
        beerService.updateBeerById(beerId, beer);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{beerId}")
    public ResponseEntity<Beer> deleteBeerById(@PathVariable("beerId") UUID beerId) {
        log.info("BeerController: deleteBeerById({})", beerId);
        beerService.deleteBeerById(beerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{beerId}")
    public ResponseEntity<Beer> patchBeerById(@PathVariable("beerId") UUID beerId, @RequestBody Beer beer) {
        log.info("BeerController: patchBeerById({})", beerId);
        beerService.patchBeerById(beerId, beer);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
