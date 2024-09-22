package com.study.spring6restmvc.controllers;

import com.study.spring6restmvc.exceptions.NotFoundException;
import com.study.spring6restmvc.model.BeerOrderDTO;
import com.study.spring6restmvc.services.BeerOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
