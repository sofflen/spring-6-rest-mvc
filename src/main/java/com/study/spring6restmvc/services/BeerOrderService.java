package com.study.spring6restmvc.services;

import com.study.spring6restmvcapi.model.BeerOrderDTO;
import com.study.spring6restmvcapi.model.BeerOrderRequestBodyDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface BeerOrderService {

    Optional<BeerOrderDTO> getById(UUID beerOrderId);

    Page<BeerOrderDTO> getAll(Integer pageNumber, Integer pageSize);

    BeerOrderDTO save(BeerOrderRequestBodyDTO beerOrderCreateDto);

    Optional<BeerOrderDTO> updateById(UUID beerOrderId, BeerOrderRequestBodyDTO beerOrderUpdateDto);

    Optional<BeerOrderDTO> patchById(UUID beerOrderId, BeerOrderRequestBodyDTO beerOrderPatchDto);

    boolean deleteById(UUID beerOrderId);
}
