package com.study.spring6restmvc.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Builder
@Data
public class BeerOrderRequestBodyDTO {
    private String customerRef;
    @NotNull
    private UUID customerId;
    private Set<BeerOrderLineRequestBodyDTO> orderLines;
    private BeerOrderShipmentDTO beerOrderShipment;
}
