package com.study.spring6restmvc.mappers;

import com.study.spring6restmvc.entities.BeerOrderLine;
import com.study.spring6restmvc.model.BeerOrderLineDTO;
import org.mapstruct.Mapper;

@Mapper(uses = {BeerMapper.class, BeerOrderMapper.class})
public interface BeerOrderLineMapper {

    BeerOrderLine beerOrderLineDtoToBeerOrderLine(BeerOrderLineDTO dto);

    BeerOrderLineDTO beerOrderLineToBeerOrderLineDto(BeerOrderLine beerOrderLine);
}
