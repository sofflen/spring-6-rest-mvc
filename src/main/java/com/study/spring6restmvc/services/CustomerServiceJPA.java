package com.study.spring6restmvc.services;

import com.study.spring6restmvc.entities.Customer;
import com.study.spring6restmvc.mappers.CustomerMapper;
import com.study.spring6restmvc.model.CustomerDTO;
import com.study.spring6restmvc.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.util.StringUtils.hasText;

@Service
@Primary
@RequiredArgsConstructor
public class CustomerServiceJPA implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public Optional<CustomerDTO> getCustomerById(UUID id) {
        return Optional.ofNullable(
                customerMapper.customerToCustomerDTO(
                        customerRepository.findById(id).orElse(null)));
    }

    @Override
    public List<CustomerDTO> getAllCustomers(String customerName, String email) {
        List<Customer> customerList;

        if (hasText(customerName) && hasText(email)) {
            customerList = getCustomersByNameAndEmail(customerName, email);
        } else if (hasText(customerName)) {
            customerList = getCustomersByName(customerName);
        } else if (hasText(email)) {
            customerList = getCustomersByEmail(email);
        } else {
            customerList = customerRepository.findAll();
        }

        return customerList.stream()
                .map(customerMapper::customerToCustomerDTO)
                .toList();
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customer) {
        return customerMapper.customerToCustomerDTO(
                customerRepository.save(
                        customerMapper.customerDtoToCustomer(customer)));
    }

    @Override
    public Optional<CustomerDTO> updateCustomerById(UUID customerId, CustomerDTO customer) {
        AtomicReference<CustomerDTO> atomicReference = new AtomicReference<>();

        customerRepository.findById(customerId).ifPresentOrElse(
                foundCustomer -> {
                    if (StringUtils.hasText(customer.getCustomerName()))
                        foundCustomer.setCustomerName(customer.getCustomerName());
                    foundCustomer.setUpdatedAt(LocalDateTime.now());

                    atomicReference.set(
                            customerMapper.customerToCustomerDTO(
                                    customerRepository.save(foundCustomer)));
                },
                () -> atomicReference.set(null));

        return Optional.ofNullable(atomicReference.get());
    }

    @Override
    public boolean deleteCustomerById(UUID customerId) {
        if (customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
            return true;
        }
        return false;
    }

    @Override
    public Optional<CustomerDTO> patchCustomerById(UUID customerId, CustomerDTO customer) {
        AtomicReference<CustomerDTO> atomicReference = new AtomicReference<>();

        customerRepository.findById(customerId).ifPresentOrElse(
                foundCustomer -> {
                    if (customer.getCustomerName() != null)
                        foundCustomer.setCustomerName(customer.getCustomerName());
                    foundCustomer.setUpdatedAt(LocalDateTime.now());

                    atomicReference.set(
                            customerMapper.customerToCustomerDTO(
                                    customerRepository.save(foundCustomer)));
                },
                () -> atomicReference.set(null));

        return Optional.ofNullable(atomicReference.get());
    }

    private List<Customer> getCustomersByNameAndEmail(String customerName, String email) {
        return customerRepository
                .findAllByCustomerNameIsLikeIgnoreCaseAndEmail("%" + customerName + "%", email);
    }

    private List<Customer> getCustomersByName(String customerName) {
        return customerRepository.findAllByCustomerNameIsLikeIgnoreCase("%" + customerName + "%");
    }

    private List<Customer> getCustomersByEmail(String email) {
        return customerRepository.findAllByEmail(email);
    }
}
