package com.study.spring6restmvc.entities;

import com.study.spring6restmvc.model.BeerStyle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class BeerAudit {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "UUID", length = 36, updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private UUID auditId;
    @CreationTimestamp
    private LocalDateTime auditCreatedAt;
    @UpdateTimestamp
    private LocalDateTime auditUpdatedAt;
    private String auditPrincipalName;
    private String auditEventType;

    private UUID id;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Size(max = 50)
    @Column(length = 50)
    private String beerName;
    @Size(max = 255)
    private String upc;
    private BeerStyle beerStyle;
    private Integer quantityOnHand;
    private BigDecimal price;
}
