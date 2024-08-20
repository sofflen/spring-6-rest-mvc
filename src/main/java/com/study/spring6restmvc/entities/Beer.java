package com.study.spring6restmvc.entities;

import com.study.spring6restmvc.model.BeerStyle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Beer {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "varchar(36)", length = 36, updatable = false, nullable = false)
    private UUID id;
    @Version
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @NotBlank
    @Size(max = 50)
    @Column(length = 50)
    private String beerName;
    @NotBlank
    @Size(max = 255)
    private String upc;
    @NotNull
    private BeerStyle beerStyle;
    private Integer quantityOnHand;
    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    private BigDecimal price;
}
