package com.study.spring6restmvc.controllers;

import com.study.spring6restmvc.model.Customer;
import com.study.spring6restmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public List<Customer> getAllCustomers() {
        log.info("CustomerController: getAllCustomers()");
        return customerService.getAllCustomers();
    }

    @GetMapping("/{customerId}")
    public Customer getCustomerById(@PathVariable("customerId") UUID customerId) {
        log.info("CustomerController: getCustomerById({})", customerId);
        return customerService.getCustomerById(customerId);
    }
}

