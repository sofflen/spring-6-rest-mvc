package com.study.spring6restmvc.services;

import com.study.spring6restmvc.model.BeerOrderRequestBodyDTO;
import com.study.spring6restmvc.model.BeerOrderDTO;
import com.study.spring6restmvc.model.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class BeerOrderServiceImpl implements BeerOrderService {

    private final Map<UUID, BeerOrderDTO> beerOrdersMap = new HashMap<>();

    public BeerOrderServiceImpl() {
        BeerOrderDTO beerOrder1 = BeerOrderDTO.builder()
                .id(UUID.randomUUID())
                .version(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        BeerOrderDTO beerOrder2 = BeerOrderDTO.builder()
                .id(UUID.randomUUID())
                .version(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        beerOrdersMap.put(beerOrder1.getId(), beerOrder1);
        beerOrdersMap.put(beerOrder2.getId(), beerOrder2);
    }

    @Override
    public Optional<BeerOrderDTO> getById(UUID beerOrderId) {
        return Optional.ofNullable(beerOrdersMap.get(beerOrderId));
    }

    @Override
    public Page<BeerOrderDTO> getAll(Integer pageNumber, Integer pageSize) {
        return new PageImpl<>(new ArrayList<>(beerOrdersMap.values()));
    }

    @Override
    public BeerOrderDTO save(BeerOrderRequestBodyDTO beerOrderCreateDto) {
        var beerOrderDto = BeerOrderDTO.builder()
                .id(UUID.randomUUID())
                .version(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .customer(
                        CustomerDTO.builder()
                                .id(beerOrderCreateDto.getCustomerId())
                                .customerName("Beer Order Customer")
                                .email("beer.order@fake.com")
                                .build())
                .build();

        beerOrdersMap.put(beerOrderDto.getId(), beerOrderDto);

        return beerOrderDto;
    }

    @Override
    public Optional<BeerOrderDTO> updateById(UUID beerOrderId, BeerOrderRequestBodyDTO beerOrderUpdateDto) {
        var existingBeerOrder = beerOrdersMap.get(beerOrderId);

        existingBeerOrder.setCustomer(
                CustomerDTO.builder()
                        .id(beerOrderUpdateDto.getCustomerId())
                        .customerName("Beer Order Customer")
                        .email("beer.order@fake.com")
                        .build());
        existingBeerOrder.setCustomerRef(beerOrderUpdateDto.getCustomerRef());
        existingBeerOrder.setBeerOrderShipment(beerOrderUpdateDto.getBeerOrderShipment());
        existingBeerOrder.setUpdatedAt(LocalDateTime.now());

        return Optional.of(existingBeerOrder);
    }

    @Override
    public boolean deleteById(UUID beerOrderId) {
        beerOrdersMap.remove(beerOrderId);
        return true;
    }
}
