package com.study.spring6restmvc.services;

import com.study.spring6restmvc.entities.BeerOrder;
import com.study.spring6restmvc.entities.BeerOrderLine;
import com.study.spring6restmvc.exceptions.NotFoundException;
import com.study.spring6restmvc.mappers.BeerOrderMapper;
import com.study.spring6restmvc.model.BeerOrderRequestBodyDTO;
import com.study.spring6restmvc.model.BeerOrderDTO;
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

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

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
    public BeerOrderDTO save(BeerOrderRequestBodyDTO beerOrderCreateDTO) throws NotFoundException {
        log.info("BeerOrderService: save({})", beerOrderCreateDTO);
        var customer = customerRepository
                .findById(beerOrderCreateDTO.getCustomerId())
                .orElseThrow(NotFoundException::new);


        var beerOrderLines = new HashSet<BeerOrderLine>();

        if (beerOrderCreateDTO.getOrderLines() != null) {
            beerOrderCreateDTO.getOrderLines()
                    .forEach(orderLine -> BeerOrderLine.builder()
                            .beer(beerRepository
                                    .findById(orderLine.getBeerId())
                                    .orElseThrow(NotFoundException::new))
                            .orderQuantity(orderLine.getOrderQuantity())
                            .build());
        }

        var beerOrder = BeerOrder.builder()
                .customer(customer)
                .customerRef(beerOrderCreateDTO.getCustomerRef())
                .beerOrderLines(beerOrderLines)
                .build();

        var savedBeerOrder = beerOrderRepository.save(beerOrder);

        return beerOrderMapper.beerOrderToBeerOrderDto(savedBeerOrder);
    }
}
