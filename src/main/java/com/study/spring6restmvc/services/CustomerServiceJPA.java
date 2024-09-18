package com.study.spring6restmvc.services;

import com.study.spring6restmvc.entities.Customer;
import com.study.spring6restmvc.mappers.CustomerMapper;
import com.study.spring6restmvc.model.CustomerDTO;
import com.study.spring6restmvc.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.util.StringUtils.hasText;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceJPA implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CacheManager cacheManager;

    @Override
    @Cacheable(cacheNames = "customerCache")
    public Optional<CustomerDTO> getCustomerById(UUID id) {
        log.info("CustomerService: getCustomerById({})", id);

        return Optional.ofNullable(
                customerMapper.customerToCustomerDTO(
                        customerRepository.findById(id).orElse(null)));
    }

    @Override
    @Cacheable(cacheNames = "customerListCache", condition = "pageNumber == null || pageSize == null")
    public Page<CustomerDTO> getAllCustomers(String customerName, String email, Integer pageNumber, Integer pageSize) {
        log.info("CustomerService: getAllCustomers");

        PageRequest pageRequest = ServiceUtils.buildPageRequest(pageNumber, pageSize, Sort.by("customerName"));
        Page<Customer> customerPage;

        if (hasText(customerName) && hasText(email)) {
            customerPage = getCustomersByNameAndEmail(customerName, email, pageRequest);
        } else if (hasText(customerName)) {
            customerPage = getCustomersByName(customerName, pageRequest);
        } else if (hasText(email)) {
            customerPage = getCustomersByEmail(email, pageRequest);
        } else {
            customerPage = customerRepository.findAll(pageRequest);
        }

        return customerPage.map(customerMapper::customerToCustomerDTO);
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customer) {
        clearCache(null);
        return customerMapper.customerToCustomerDTO(
                customerRepository.save(
                        customerMapper.customerDtoToCustomer(customer)));
    }

    @Override
    public Optional<CustomerDTO> updateCustomerById(UUID customerId, CustomerDTO customer) {
        AtomicReference<CustomerDTO> atomicReference = new AtomicReference<>();

        customerRepository.findById(customerId).ifPresentOrElse(
                foundCustomer -> {
                    clearCache(customerId);

                    if (StringUtils.hasText(customer.getCustomerName())) {
                        foundCustomer.setCustomerName(customer.getCustomerName());
                    }
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
            clearCache(customerId);
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
                    clearCache(customerId);

                    if (customer.getCustomerName() != null) {
                        foundCustomer.setCustomerName(customer.getCustomerName());
                    }
                    foundCustomer.setUpdatedAt(LocalDateTime.now());

                    atomicReference.set(
                            customerMapper.customerToCustomerDTO(
                                    customerRepository.save(foundCustomer)));
                },
                () -> atomicReference.set(null));

        return Optional.ofNullable(atomicReference.get());
    }

    private Page<Customer> getCustomersByNameAndEmail(String customerName, String email, Pageable pageable) {
        return customerRepository
                .findAllByCustomerNameIsLikeIgnoreCaseAndEmail("%" + customerName + "%", email, pageable);
    }

    private Page<Customer> getCustomersByName(String customerName, Pageable pageable) {
        return customerRepository
                .findAllByCustomerNameIsLikeIgnoreCase("%" + customerName + "%", pageable);
    }

    private Page<Customer> getCustomersByEmail(String email, Pageable pageable) {
        return customerRepository.findAllByEmail(email, pageable);
    }

    private void clearCache(UUID customerId) {
        Cache beerListCache = cacheManager.getCache("customerListCache");

        if (beerListCache != null) {
            beerListCache.clear();
        }

        if (customerId != null) {
            Cache beerCache = cacheManager.getCache("customerCache");

            if (beerCache != null) {
                beerCache.evict(customerId);
            }
        }
    }
}
