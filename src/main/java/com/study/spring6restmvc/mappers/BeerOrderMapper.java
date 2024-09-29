package com.study.spring6restmvc.mappers;

import com.study.spring6restmvc.entities.BeerOrder;
import com.study.spring6restmvcapi.model.BeerOrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {CustomerMapper.class, BeerOrderShipmentMapper.class})
public interface BeerOrderMapper {

    @Mapping(target = "beerOrderLines", ignore = true)
    BeerOrder beerOrderDtoToBeerOrder(BeerOrderDTO dto);

    BeerOrderDTO beerOrderToBeerOrderDto(BeerOrder beerOrder);
}
