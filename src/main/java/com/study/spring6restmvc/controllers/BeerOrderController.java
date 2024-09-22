package com.study.spring6restmvc.controllers;

import com.study.spring6restmvc.exceptions.NotFoundException;
import com.study.spring6restmvc.model.BeerDTO;
import com.study.spring6restmvc.model.BeerOrderDTO;
import com.study.spring6restmvc.model.BeerOrderRequestBodyDTO;
import com.study.spring6restmvc.services.BeerOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BeerOrderController {

    private final BeerOrderService beerOrderService;

    public static final String BEER_ORDER_PATH = "/api/v1/order";
    public static final String BEER_ORDER_ID_PATH = BEER_ORDER_PATH + "/{id}";

    @GetMapping(BEER_ORDER_PATH)
    public PagedModel<BeerOrderDTO> getAllBeerOrders(@RequestParam(required = false) Integer pageNumber,
                                                     @RequestParam(required = false) Integer pageSize) {
        log.info("BeerOrderController: getAllBeerOrders()");

        var beerOrderPage = beerOrderService.getAll(pageNumber, pageSize);

        if (beerOrderPage.isEmpty()) {
            throw new NotFoundException("Beer orders not found");
        }

        return new PagedModel<>(beerOrderPage);
    }

    @GetMapping(BEER_ORDER_ID_PATH)
    public BeerOrderDTO getBeerOrderById(@PathVariable("id") UUID beerOrderId) {
        log.info("BeerOrderController: getBeerOrderById({})", beerOrderId);

        return beerOrderService.getById(beerOrderId).orElseThrow(NotFoundException::new);
    }

    @PostMapping(BEER_ORDER_PATH)
    public ResponseEntity<BeerOrderDTO> createBeerOrder(@Validated @RequestBody BeerOrderRequestBodyDTO beerOrder) {
        log.info("BeerOrderController: createBeerOrder({})", beerOrder);

        BeerOrderDTO savedBeerOrder = beerOrderService.save(beerOrder);
        HttpHeaders headers = new HttpHeaders();

        headers.add("Location", BEER_ORDER_PATH + "/" + savedBeerOrder.getId());

        return new ResponseEntity<>(savedBeerOrder, headers, HttpStatus.CREATED);
    }

    @PutMapping(BEER_ORDER_ID_PATH)
    public ResponseEntity<BeerOrderDTO> updateBeerOrderById(@PathVariable("id") UUID beerOrderId,
                                                            @Validated @RequestBody BeerOrderRequestBodyDTO beerOrderDto) {
        log.info("BeerOrderController: updateBeerOrderById({})", beerOrderId);

        if (beerOrderService.updateById(beerOrderId, beerOrderDto).isEmpty()) {
            throw new NotFoundException();
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(BEER_ORDER_ID_PATH)
    public ResponseEntity<BeerDTO> deleteBeerOrderById(@PathVariable("id") UUID beerId) {
        log.info("BeerOrderController: deleteBeerOrderById({})", beerId);

        if (!beerOrderService.deleteById(beerId)) {
            throw new NotFoundException();
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
