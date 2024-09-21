package com.study.spring6restmvc.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class BeerOrderLineDTO {
    private UUID id;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer orderQuantity;
    private Integer quantityAllocated;
    private BeerDTO beer;
    private BeerOrderDTO beerOrder;
}
