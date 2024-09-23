package com.study.spring6restmvc.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BeerOrderLine {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "UUID", length = 36, updatable = false, nullable = false)
    private UUID id;
    @Version
    private Integer version;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Min(value = 1, message = "Quantity must be more than 0")
    private Integer orderQuantity;
    private Integer quantityAllocated;
    @ManyToOne
    @JoinColumn(name = "beer_id")
    private Beer beer;
    @ManyToOne
    @JoinColumn(name = "beer_order_id")
    private BeerOrder beerOrder;

    public BeerOrderLine(UUID id, Integer version, LocalDateTime createdAt, LocalDateTime updatedAt,
                         Integer orderQuantity, Integer quantityAllocated, Beer beer, BeerOrder beerOrder) {
        this.id = id;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;
        this.setBeer(beer);
        this.setBeerOrder(beerOrder);
    }

    public void setBeer(Beer beer) {
        if (beer != null) {
            this.beer = beer;
            beer.getBeerOrderLines().add(this);
        }
    }

    public void setBeerOrder(BeerOrder beerOrder) {
        if (beerOrder != null) {
            this.beerOrder = beerOrder;
            beerOrder.getBeerOrderLines().add(this);
        }
    }
}
