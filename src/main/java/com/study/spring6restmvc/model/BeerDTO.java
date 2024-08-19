package com.study.spring6restmvc.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class BeerDTO {
    private UUID id;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String beerName;
    private String upc;
    private BeerStyle beerStyle;
    private Integer quantityOnHand;
    private BigDecimal price;
}
