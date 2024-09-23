package com.study.spring6restmvc.services;

import com.study.spring6restmvc.entities.BeerOrder;
import com.study.spring6restmvc.entities.BeerOrderLine;
import com.study.spring6restmvc.entities.BeerOrderShipment;
import com.study.spring6restmvc.exceptions.NotFoundException;
import com.study.spring6restmvc.mappers.BeerOrderMapper;
import com.study.spring6restmvc.model.BeerOrderDTO;
import com.study.spring6restmvc.model.BeerOrderRequestBodyDTO;
import com.study.spring6restmvc.repositories.BeerOrderRepository;
import com.study.spring6restmvc.repositories.BeerRepository;
import com.study.spring6restmvc.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class BeerOrderServiceJPA implements BeerOrderService {

    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final BeerRepository beerRepository;
    private final BeerOrderMapper beerOrderMapper;

    @Override
    public Optional<BeerOrderDTO> getById(UUID beerOrderId) {
        log.info("BeerOrderService: getById({})", beerOrderId);

        return Optional.ofNullable(
                beerOrderMapper.beerOrderToBeerOrderDto(
                        beerOrderRepository.findById(beerOrderId).orElse(null)));
    }

    @Override
    public Page<BeerOrderDTO> getAll(Integer pageNumber, Integer pageSize) {
        log.info("BeerOrderService: getAll()");

        PageRequest pageRequest = ServiceUtils.buildPageRequest(pageNumber, pageSize, Sort.unsorted());
        Page<BeerOrder> beerOrders = beerOrderRepository.findAll(pageRequest);

        return beerOrders.map(beerOrderMapper::beerOrderToBeerOrderDto);
    }

    @Override
    public BeerOrderDTO save(BeerOrderRequestBodyDTO beerOrderCreateDto) throws NotFoundException {
        log.info("BeerOrderService: save({})", beerOrderCreateDto);
        var customer = customerRepository
                .findById(beerOrderCreateDto.getCustomerId())
                .orElseThrow(NotFoundException::new);


        var beerOrderLines = new HashSet<BeerOrderLine>();

        if (beerOrderCreateDto.getOrderLines() != null) {
            beerOrderCreateDto.getOrderLines()
                    .forEach(orderLine -> BeerOrderLine.builder()
                            .beer(beerRepository
                                    .findById(orderLine.getBeerId())
                                    .orElseThrow(NotFoundException::new))
                            .orderQuantity(orderLine.getOrderQuantity())
                            .build());
        }

        var beerOrder = BeerOrder.builder()
                .customer(customer)
                .customerRef(beerOrderCreateDto.getCustomerRef())
                .beerOrderLines(beerOrderLines)
                .build();

        var savedBeerOrder = beerOrderRepository.save(beerOrder);

        return beerOrderMapper.beerOrderToBeerOrderDto(savedBeerOrder);
    }

    @Override
    public Optional<BeerOrderDTO> updateById(UUID beerOrderId, BeerOrderRequestBodyDTO beerOrderUpdateDto) {
        AtomicReference<BeerOrderDTO> atomicReference = new AtomicReference<>();

        beerOrderRepository.findById(beerOrderId).ifPresentOrElse(
                foundBeerOrder -> {
                    foundBeerOrder.setCustomer(customerRepository
                            .findById(beerOrderUpdateDto.getCustomerId())
                            .orElseThrow(NotFoundException::new));
                    foundBeerOrder.setCustomerRef(beerOrderUpdateDto.getCustomerRef());
                    foundBeerOrder.setUpdatedAt(LocalDateTime.now());

                    updateBeerOrderLines(beerOrderUpdateDto, foundBeerOrder);

                    updateBeerOrderShipment(beerOrderUpdateDto, foundBeerOrder);

                    var updatedBeerOrder = beerOrderRepository.save(foundBeerOrder);

                    atomicReference.set(beerOrderMapper.beerOrderToBeerOrderDto(updatedBeerOrder));
                },
                () -> atomicReference.set(null));

        return Optional.ofNullable(atomicReference.get());
    }

    @Override
    public Optional<BeerOrderDTO> patchById(UUID beerOrderId, BeerOrderRequestBodyDTO beerOrderPatchDto) {
        AtomicReference<BeerOrderDTO> atomicReference = new AtomicReference<>();

        beerOrderRepository.findById(beerOrderId).ifPresentOrElse(
                foundBeerOrder -> {
                    if (beerOrderPatchDto.getCustomerRef() != null) {
                        foundBeerOrder.setCustomer(customerRepository
                                .findById(beerOrderPatchDto.getCustomerId())
                                .orElseThrow(NotFoundException::new));
                    }
                    if (beerOrderPatchDto.getCustomerRef() != null) {
                        foundBeerOrder.setCustomerRef(beerOrderPatchDto.getCustomerRef());
                    }

                    updateBeerOrderLines(beerOrderPatchDto, foundBeerOrder);

                    updateBeerOrderShipment(beerOrderPatchDto, foundBeerOrder);

                    var patchedBeerOrder = beerOrderRepository.save(foundBeerOrder);

                    atomicReference.set(beerOrderMapper.beerOrderToBeerOrderDto(patchedBeerOrder));
                },
                () -> atomicReference.set(null));

        return Optional.ofNullable(atomicReference.get());
    }

    @Override
    public boolean deleteById(UUID beerOrderId) {
        if (beerOrderRepository.existsById(beerOrderId)) {
            beerOrderRepository.deleteById(beerOrderId);
            return true;
        }
        return false;
    }

    private void updateBeerOrderLines(BeerOrderRequestBodyDTO beerOrderUpdateDto, BeerOrder foundBeerOrder) {
        if (beerOrderUpdateDto.getOrderLines() == null) {
            return;
        }

        Set<BeerOrderLine> lines = new HashSet<>();

        beerOrderUpdateDto.getOrderLines()
                .forEach(orderLine -> {
                    var line = BeerOrderLine.builder()
                            .beer(beerRepository
                                    .findById(orderLine.getBeerId())
                                    .orElseThrow(NotFoundException::new))
                            .orderQuantity(orderLine.getOrderQuantity())
                            .quantityAllocated(orderLine.getQuantityAllocated())
                            .build();
                    lines.add(line);
                });

        foundBeerOrder.setBeerOrderLines(lines);
    }

    private void updateBeerOrderShipment(BeerOrderRequestBodyDTO beerOrderUpdateDto, BeerOrder foundBeerOrder) {
        if (beerOrderUpdateDto.getBeerOrderShipment() == null
                || beerOrderUpdateDto.getBeerOrderShipment().getTrackingNumber() == null) {
            return;
        }

        var trackingNumber = beerOrderUpdateDto.getBeerOrderShipment().getTrackingNumber();

        if (foundBeerOrder.getBeerOrderShipment() == null) {
            foundBeerOrder.setBeerOrderShipment(BeerOrderShipment.builder()
                    .trackingNumber(trackingNumber)
                    .build());
        } else {
            foundBeerOrder.getBeerOrderShipment().setTrackingNumber(trackingNumber);
        }
    }
}
