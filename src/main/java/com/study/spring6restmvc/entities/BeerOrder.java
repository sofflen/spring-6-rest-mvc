package com.study.spring6restmvc.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BeerOrder {
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
    private String customerRef;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @OneToMany(mappedBy = "beerOrder", cascade = CascadeType.PERSIST)
    private Set<BeerOrderLine> beerOrderLines = new HashSet<>();
    @OneToOne
    @JoinColumn(name = "beer_order_shipment_id")
    private BeerOrderShipment beerOrderShipment;

    public BeerOrder(UUID id, Integer version, LocalDateTime createdAt, LocalDateTime updatedAt, String customerRef,
                     Customer customer, Set<BeerOrderLine> beerOrderLines, BeerOrderShipment beerOrderShipment) {
        this.id = id;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.customerRef = customerRef;
        this.setCustomer(customer);
        this.setBeerOrderLines(beerOrderLines);
        this.setBeerOrderShipment(beerOrderShipment);
    }

    public void setCustomer(Customer customer) {
        if (customer != null) {
            this.customer = customer;
            customer.getBeerOrders().add(this);
        }
    }

    public void setBeerOrderShipment(BeerOrderShipment beerOrderShipment) {
        if (beerOrderShipment != null) {
            this.beerOrderShipment = beerOrderShipment;
            beerOrderShipment.setBeerOrder(this);
        }
    }

    public void setBeerOrderLines(Set<BeerOrderLine> beerOrderLines) {
        if (beerOrderLines != null) {
            beerOrderLines.forEach(beerOrderLine -> {
                this.beerOrderLines.add(beerOrderLine);
                beerOrderLine.setBeerOrder(this);
            });
        }
    }

    public void addBeerOrderLines(BeerOrderLine... beerOrderLines) {
        for (BeerOrderLine beerOrderLine : beerOrderLines) {
            this.beerOrderLines.add(beerOrderLine);
            beerOrderLine.setBeerOrder(this);
        }
    }
}
