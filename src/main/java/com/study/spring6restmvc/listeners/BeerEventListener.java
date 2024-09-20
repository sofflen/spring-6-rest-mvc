package com.study.spring6restmvc.listeners;

import com.study.spring6restmvc.events.beer.BeerCreatedEvent;
import com.study.spring6restmvc.events.beer.BeerDeletedEvent;
import com.study.spring6restmvc.events.beer.BeerEvent;
import com.study.spring6restmvc.events.beer.BeerPatchedEvent;
import com.study.spring6restmvc.events.beer.BeerUpdatedEvent;
import com.study.spring6restmvc.mappers.BeerMapper;
import com.study.spring6restmvc.repositories.BeerAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.study.spring6restmvc.listeners.EventTypes.CREATED;
import static com.study.spring6restmvc.listeners.EventTypes.DELETED;
import static com.study.spring6restmvc.listeners.EventTypes.PATCHED;
import static com.study.spring6restmvc.listeners.EventTypes.UPDATED;

@Component
@Slf4j
@RequiredArgsConstructor
public class BeerEventListener {

    private final BeerAuditRepository beerAuditRepository;
    private final BeerMapper beerMapper;

    @EventListener
    @Async
    public void listen(BeerEvent event) {
        String auditEventType;

        switch (event) {
            case BeerCreatedEvent ignored -> auditEventType = CREATED.name();
            case BeerUpdatedEvent ignored -> auditEventType = UPDATED.name();
            case BeerDeletedEvent ignored -> auditEventType = DELETED.name();
            case BeerPatchedEvent ignored -> auditEventType = PATCHED.name();
            default -> auditEventType = "UNKNOWN";
        }

        var beerAudit = beerMapper.beerToBeerAudit(event.getBeer());
        beerAudit.setAuditEventType(auditEventType);

        if (event.getAuthentication() != null && event.getAuthentication().getName() != null) {
            beerAudit.setAuditPrincipalName(event.getAuthentication().getName());
        }

        var savedBeerAudit = beerAuditRepository.save(beerAudit);
        log.debug("{} Beer Audit saved: id {}", auditEventType, savedBeerAudit.getAuditId());
    }
}
