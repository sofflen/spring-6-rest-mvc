package com.study.spring6restmvc.services;

import com.study.spring6restmvc.model.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private final Map<UUID, CustomerDTO> customersMap = new HashMap<>();

    public CustomerServiceImpl() {
        CustomerDTO customer1 = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .customerName("John Doe")
                .version(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CustomerDTO customer2 = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .customerName("Jane Doe")
                .version(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CustomerDTO customer3 = CustomerDTO.builder()
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
    public Optional<CustomerDTO> getCustomerById(UUID id) {
        log.info("CustomerService: GetCustomerById({})", id);

        return Optional.ofNullable(customersMap.get(id));
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        log.info("CustomerService: GetAllCustomers()");

        return new ArrayList<>(customersMap.values());
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customer) {
        CustomerDTO savedCustomer = CustomerDTO.builder()
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
    public void updateCustomerById(UUID id, CustomerDTO customer) {
        CustomerDTO existingCustomer = customersMap.get(id);

        log.info("CustomerService: UpdateCustomer({})\nCustomer before update: {}", id, existingCustomer);

        existingCustomer.setCustomerName(customer.getCustomerName());
        existingCustomer.setVersion(existingCustomer.getVersion() + 1);

        log.info("Customer after update: {}", existingCustomer);
    }

    @Override
    public void deleteCustomerById(UUID customerId) {
        log.info("CustomerService: DeleteCustomerById({})", customerId);

        CustomerDTO deletedCustomer = customersMap.remove(customerId);

        log.info("CustomerService: deleteCustomerById deletedCustomer: {}", deletedCustomer);
    }

    @Override
    public void patchCustomerById(UUID customerId, CustomerDTO customer) {
        log.info("CustomerService: PatchCustomerById({})", customerId);

        CustomerDTO existingCustomer = customersMap.get(customerId);

        if (customer.getCustomerName() != null)
            existingCustomer.setCustomerName(customer.getCustomerName());

        existingCustomer.setVersion(existingCustomer.getVersion() + 1);
        existingCustomer.setUpdatedAt(LocalDateTime.now());

        log.info("CustomerService: PatchCustomerById patchedCustomer: {}", existingCustomer);
    }
}
