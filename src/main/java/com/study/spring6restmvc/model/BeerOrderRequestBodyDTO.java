package com.study.spring6restmvc.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Builder
@Data
public class BeerOrderRequestBodyDTO {
    private String customerRef;
    private UUID customerId;
    private Set<BeerOrderLineRequestBodyDTO> orderLines;
    private BeerOrderShipmentDTO beerOrderShipment;
}
