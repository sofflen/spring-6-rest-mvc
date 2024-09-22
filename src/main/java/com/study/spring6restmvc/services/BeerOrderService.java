package com.study.spring6restmvc.services;

import com.study.spring6restmvc.model.BeerOrderDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface BeerOrderService {

    Optional<BeerOrderDTO> getById(UUID beerOrderId);

    Page<BeerOrderDTO> getAll(Integer pageNumber, Integer pageSize);

}
