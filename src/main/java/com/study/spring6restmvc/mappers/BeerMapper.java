package com.study.spring6restmvc.mappers;

import com.study.spring6restmvc.entities.Beer;
import com.study.spring6restmvc.entities.BeerAudit;
import com.study.spring6restmvcapi.model.BeerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BeerMapper {

    @Mapping(target = "beerOrderLines", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Beer beerDtoToBeer(BeerDTO dto);

    BeerDTO beerToBeerDTO(Beer beer);

    @Mapping(target = "auditId", ignore = true)
    @Mapping(target = "auditCreatedAt", ignore = true)
    @Mapping(target = "auditUpdatedAt", ignore = true)
    @Mapping(target = "auditPrincipalName", ignore = true)
    @Mapping(target = "auditEventType", ignore = true)
    BeerAudit beerToBeerAudit(Beer beer);
}
