package com.study.spring6restmvc.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
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
public class Customer {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "UUID", length = 36, updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private UUID id;
    @Version
    private Integer version;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(updatable = false)
    private LocalDateTime updatedAt;
    @NotBlank
    @Size(max = 50)
    @Column(length = 50)
    private String customerName;
    private String email;
    @OneToMany(mappedBy = "customer")
    @Builder.Default
    private Set<BeerOrder> beerOrders = new HashSet<>();
}
