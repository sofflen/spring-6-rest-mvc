package com.study.spring6restmvc.events.customer;

import com.study.spring6restmvc.entities.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CustomerPatchedEvent implements CustomerEvent {
    private Customer customer;
    private Authentication authentication;
}
