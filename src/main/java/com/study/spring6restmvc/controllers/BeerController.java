package com.study.spring6restmvc.controllers;

import com.study.spring6restmvc.exceptions.NotFoundException;
import com.study.spring6restmvc.model.BeerDTO;
import com.study.spring6restmvc.model.BeerStyle;
import com.study.spring6restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BeerController {

    private final BeerService beerService;

    public static final String BEER_PATH = "/api/v1/beer";
    public static final String BEER_ID_PATH = BEER_PATH + "/{id}";

    @GetMapping(BEER_PATH)
    public PagedModel<BeerDTO> getAllBeers(@RequestParam(required = false) String beerName,
                                           @RequestParam(required = false) BeerStyle beerStyle,
                                           @RequestParam(required = false) Boolean showInventory,
                                           @RequestParam(required = false) Integer pageNumber,
                                           @RequestParam(required = false) Integer pageSize) {
        log.info("BeerController: getAllBeers()");

        var beerPage = beerService.getAllBeers(beerName, beerStyle, showInventory, pageNumber, pageSize);

        if (beerPage.isEmpty() && (beerName != null || beerStyle != null)) {
            throw new NotFoundException("Beer not found");
        }

        return new PagedModel<>(beerPage);
    }

    @GetMapping(BEER_ID_PATH)
    public BeerDTO getBeerById(@PathVariable("id") UUID beerId) {
        log.info("BeerController: getBeerById({})", beerId);

        return beerService.getBeerById(beerId).orElseThrow(NotFoundException::new);
    }

    @PostMapping(BEER_PATH)
    public ResponseEntity<BeerDTO> createBeer(@Validated @RequestBody BeerDTO beer) {
        log.info("BeerController: createBeer({})", beer);

        BeerDTO savedBeer = beerService.saveBeer(beer);
        HttpHeaders headers = new HttpHeaders();

        headers.add("Location", BEER_PATH + "/" + savedBeer.getId());

        return new ResponseEntity<>(savedBeer, headers, HttpStatus.CREATED);
    }

    @PutMapping(BEER_ID_PATH)
    public ResponseEntity<BeerDTO> updateBeerById(@PathVariable("id") UUID beerId, @Validated @RequestBody BeerDTO beer) {
        log.info("BeerController: updateBeerById({})", beerId);

        if (beerService.updateBeerById(beerId, beer).isEmpty()) {
            throw new NotFoundException();
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(BEER_ID_PATH)
    public ResponseEntity<BeerDTO> deleteBeerById(@PathVariable("id") UUID beerId) {
        log.info("BeerController: deleteBeerById({})", beerId);

        if (!beerService.deleteBeerById(beerId)) {
            throw new NotFoundException();
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(BEER_ID_PATH)
    public ResponseEntity<BeerDTO> patchBeerById(@PathVariable("id") UUID beerId, @RequestBody BeerDTO beer) {
        log.info("BeerController: patchBeerById({})", beerId);

        if (beerService.patchBeerById(beerId, beer).isEmpty()) {
            throw new NotFoundException();
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
