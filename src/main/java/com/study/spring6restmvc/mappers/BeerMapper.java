package com.study.spring6restmvc.mappers;

import com.study.spring6restmvc.entities.Beer;
import com.study.spring6restmvc.model.BeerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {

    Beer beerDtoToBeer(BeerDTO dto);

    BeerDTO beerToBeerDTO(Beer beer);
}
