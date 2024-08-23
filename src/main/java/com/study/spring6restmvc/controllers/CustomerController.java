package com.study.spring6restmvc.controllers;

import com.study.spring6restmvc.exceptions.NotFoundException;
import com.study.spring6restmvc.model.CustomerDTO;
import com.study.spring6restmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CustomerController {

    private final CustomerService customerService;

    public static final String CUSTOMER_PATH = "/api/v1/customer";
    public static final String CUSTOMER_PATH_ID = "/api/v1/customer/{id}";

    @GetMapping(CUSTOMER_PATH)
    public Page<CustomerDTO> getAllCustomers(@RequestParam(required = false) String customerName,
                                             @RequestParam(required = false) String email, Integer pageNumber, Integer pageSize) {
        log.info("CustomerController: getAllCustomers()");
        var customerPage = customerService.getAllCustomers(customerName, email, pageNumber, pageSize);

        if (customerPage.isEmpty() && (customerName != null || email != null)) {
            throw new NotFoundException();
        }

        return customerPage;
    }

    @GetMapping(CUSTOMER_PATH_ID)
    public CustomerDTO getCustomerById(@PathVariable("id") UUID customerId) {
        log.info("CustomerController: getCustomerById({})", customerId);

        return customerService.getCustomerById(customerId)
                .orElseThrow(NotFoundException::new);
    }

    @PostMapping(CUSTOMER_PATH)
    public ResponseEntity<CustomerDTO> createCustomer(@Validated @RequestBody CustomerDTO customer) {
        log.info("CustomerController: createCustomer({})", customer);

        CustomerDTO savedCustomer = customerService.saveCustomer(customer);
        HttpHeaders headers = new HttpHeaders();

        headers.add("Location", CUSTOMER_PATH + "/" + savedCustomer.getId());

        return new ResponseEntity<>(savedCustomer, headers, HttpStatus.CREATED);
    }

    @PutMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<CustomerDTO> updateCustomerById(@PathVariable("id") UUID id,
                                                          @Validated @RequestBody CustomerDTO customer) {
        log.info("CustomerController: updateCustomerById({})", id);

        if (customerService.updateCustomerById(id, customer).isEmpty()) {
            throw new NotFoundException();
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<CustomerDTO> deleteCustomerById(@PathVariable("id") UUID customerId) {
        log.info("CustomerController: deleteCustomerById({})", customerId);

        if (!customerService.deleteCustomerById(customerId)) {
            throw new NotFoundException();
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<CustomerDTO> patchCustomerById(@PathVariable("id") UUID customerId,
                                                         @RequestBody CustomerDTO customer) {
        log.info("CustomerController: patchCustomerById({})", customerId);

        if (customerService.patchCustomerById(customerId, customer).isEmpty()) {
            throw new NotFoundException();
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
