package com.study.spring6restmvc.listeners;

import com.study.spring6restmvc.events.BeerCreatedEvent;
import com.study.spring6restmvc.mappers.BeerMapper;
import com.study.spring6restmvc.repositories.BeerAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BeerCreatedListener {

    private final BeerAuditRepository beerAuditRepository;
    private final BeerMapper beerMapper;

    @EventListener
    @Async
    public void listen(BeerCreatedEvent event) {
        var beerAudit = beerMapper.beerToBeerAudit(event.getBeer());
        beerAudit.setAuditEventType("BEER_CREATED");

        if (event.getAuthentication() != null && event.getAuthentication().getName() != null) {
            beerAudit.setAuditPrincipalName(event.getAuthentication().getName());
        }

        var savedBeerAudit = beerAuditRepository.save(beerAudit);
        log.debug("Beer Audit saved: id {}", savedBeerAudit.getAuditId());
    }
}
