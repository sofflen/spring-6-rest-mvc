package com.study.spring6restmvc.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Builder
@Data
public class BeerOrderCreateDTO {
    private String customerRef;
    @NotNull
    private UUID customerId;
    private Set<BeerOrderLineCreateDTO> orderLines;
}
