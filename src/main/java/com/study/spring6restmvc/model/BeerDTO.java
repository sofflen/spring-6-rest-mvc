package com.study.spring6restmvc.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank
    private String beerName;
    @NotBlank
    private String upc;
    @NotNull
    private BeerStyle beerStyle;
    private Integer quantityOnHand;
    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    private BigDecimal price;
}
