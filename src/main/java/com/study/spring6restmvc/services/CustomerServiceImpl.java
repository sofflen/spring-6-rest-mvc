package com.study.spring6restmvc.services;

import com.study.spring6restmvc.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private final Map<UUID, Customer> customersMap = new HashMap<>();

    public CustomerServiceImpl() {
        Customer customer1 = Customer.builder()
                .id(UUID.randomUUID())
                .customerName("John Doe")
                .version(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Customer customer2 = Customer.builder()
                .id(UUID.randomUUID())
                .customerName("Jane Doe")
                .version(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Customer customer3 = Customer.builder()
                .id(UUID.randomUUID())
                .customerName("Thomas Doe")
                .version(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        customersMap.put(customer1.getId(), customer1);
        customersMap.put(customer2.getId(), customer2);
        customersMap.put(customer3.getId(), customer3);
    }

    @Override
    public Customer getCustomerById(UUID id) {
        log.info("CustomerService: GetCustomerById({})", id);

        return customersMap.get(id);
    }

    @Override
    public List<Customer> getAllCustomers() {
        log.info("CustomerService: GetAllCustomers()");
        return new ArrayList<>(customersMap.values());
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        Customer savedCustomer = Customer.builder()
                .id(UUID.randomUUID())
                .version(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .customerName(customer.getCustomerName())
                .build();

        customersMap.put(savedCustomer.getId(), savedCustomer);
        log.info("CustomerService: Save Customer({})", savedCustomer);
        return savedCustomer;
    }

    @Override
    public Customer updateCustomerById(UUID id, Customer customer) {
        Customer existingCustomer = customersMap.get(id);
        log.info("CustomerService: UpdateCustomer({})\nCustomer before update: {}", id, existingCustomer);
        existingCustomer.setCustomerName(customer.getCustomerName());
        existingCustomer.setVersion(existingCustomer.getVersion() + 1);

        log.info("Customer after update: {}", existingCustomer);
        return existingCustomer;
    }

    @Override
    public void deleteCustomerById(UUID customerId) {
        log.info("CustomerService: DeleteCustomerById({})", customerId);
        Customer deletedCustomer = customersMap.remove(customerId);
        log.info("CustomerService: deleteCustomerById deletedCustomer: {}", deletedCustomer);
    }

    @Override
    public void patchCustomerById(UUID customerId, Customer customer) {
        log.info("CustomerService: PatchCustomerById({})", customerId);
        Customer existingCustomer = customersMap.get(customerId);

        if (customer.getCustomerName() != null)
            existingCustomer.setCustomerName(customer.getCustomerName());

        existingCustomer.setVersion(existingCustomer.getVersion() + 1);
        existingCustomer.setUpdatedAt(LocalDateTime.now());

        log.info("CustomerService: PatchCustomerById patchedCustomer: {}", existingCustomer);
    }
}
