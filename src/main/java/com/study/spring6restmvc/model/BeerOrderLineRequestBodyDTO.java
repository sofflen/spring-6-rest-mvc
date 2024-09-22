package com.study.spring6restmvc.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class BeerOrderLineRequestBodyDTO {
    @Min(value = 1, message = "Quantity must be more than 0")
    private Integer orderQuantity;
    private Integer quantityAllocated;
    @NotNull
    private UUID beerId;
    private UUID beerOrderId;
}
