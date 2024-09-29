package com.study.spring6restmvc.mappers;

import com.study.spring6restmvc.entities.Customer;
import com.study.spring6restmvc.entities.CustomerAudit;
import com.study.spring6restmvcapi.model.CustomerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CustomerMapper {

    @Mapping(target = "beerOrders", ignore = true)
    Customer customerDtoToCustomer(CustomerDTO dto);

    CustomerDTO customerToCustomerDTO(Customer customer);

    @Mapping(target = "auditId", ignore = true)
    @Mapping(target = "auditCreatedAt", ignore = true)
    @Mapping(target = "auditUpdatedAt", ignore = true)
    @Mapping(target = "auditPrincipalName", ignore = true)
    @Mapping(target = "auditEventType", ignore = true)
    CustomerAudit customerToCustomerAudit(Customer customer);
}
