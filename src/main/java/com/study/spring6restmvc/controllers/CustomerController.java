package com.study.spring6restmvc.controllers;

import com.study.spring6restmvc.exceptions.NotFoundException;
import com.study.spring6restmvc.model.Customer;
import com.study.spring6restmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CustomerController {

    private final CustomerService customerService;

    public static final String CUSTOMER_PATH = "/api/v1/customer";
    public static final String CUSTOMER_PATH_ID = "/api/v1/customer/{id}";

    @GetMapping(CUSTOMER_PATH)
    public List<Customer> getAllCustomers() {
        log.info("CustomerController: getAllCustomers()");
        return customerService.getAllCustomers();
    }

    @GetMapping(CUSTOMER_PATH_ID)
    public Customer getCustomerById(@PathVariable("id") UUID customerId) {
        log.info("CustomerController: getCustomerById({})", customerId);

        return customerService.getCustomerById(customerId)
                .orElseThrow(NotFoundException::new);
    }

    @PostMapping(CUSTOMER_PATH)
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        log.info("CustomerController: createCustomer({})", customer);

        Customer savedCustomer = customerService.saveCustomer(customer);
        HttpHeaders headers = new HttpHeaders();

        headers.add("Location", CUSTOMER_PATH + "/" + savedCustomer.getId());

        return new ResponseEntity<>(savedCustomer, headers, HttpStatus.CREATED);
    }

    @PutMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<Customer> updateCustomerById(@PathVariable("id") UUID id,
                                                       @RequestBody Customer customer) {
        log.info("CustomerController: updateCustomerById({})", id);

        customerService.updateCustomerById(id, customer);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<Customer> deleteCustomerById(@PathVariable("id") UUID customerId) {
        log.info("CustomerController: deleteCustomerById({})", customerId);

        customerService.deleteCustomerById(customerId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<Customer> patchCustomerById(@PathVariable("id") UUID customerId,
                                                      @RequestBody Customer customer) {
        log.info("CustomerController: patchCustomerById({})", customerId);

        customerService.patchCustomerById(customerId, customer);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
