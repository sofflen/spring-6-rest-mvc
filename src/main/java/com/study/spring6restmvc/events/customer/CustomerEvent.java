package com.study.spring6restmvc.events.customer;

import com.study.spring6restmvc.entities.Customer;
import org.springframework.security.core.Authentication;

public interface CustomerEvent {

    Customer getCustomer();

    Authentication getAuthentication();
}
