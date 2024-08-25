package com.study.spring6restmvc.entities;

import com.study.spring6restmvc.model.BeerStyle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Beer {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "UUID", length = 36, updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private UUID id;
    @Version
    private Integer version;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
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
    @OneToMany(mappedBy = "beer")
    private Set<BeerOrderLine> beerOrderLines;
    @ManyToMany(mappedBy = "beers")
    @Builder.Default
    private Set<Category> categories = new HashSet<>();
}
