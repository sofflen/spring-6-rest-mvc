package com.study.spring6restmvc.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class CustomerDTO {
    private UUID id;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @NotBlank
    @Size(max = 50)
    private String customerName;
    private String email;
}
