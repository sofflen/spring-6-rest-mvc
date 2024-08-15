package com.study.spring6restmvc.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class Beer {
    private UUID id;
    private String beerName;
    private String upc;
    private BeerStyle beerStyle;
    private Integer quantityOnHand;
    private BigDecimal price;
    private Integer version;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
