package com.study.spring6restmvc.listeners;

import com.study.spring6restmvc.events.BeerCreatedEvent;
import com.study.spring6restmvc.events.BeerDeletedEvent;
import com.study.spring6restmvc.events.BeerEvent;
import com.study.spring6restmvc.events.BeerPatchedEvent;
import com.study.spring6restmvc.events.BeerUpdatedEvent;
import com.study.spring6restmvc.mappers.BeerMapper;
import com.study.spring6restmvc.repositories.BeerAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.study.spring6restmvc.listeners.BeerEventTypes.BEER_CREATED;
import static com.study.spring6restmvc.listeners.BeerEventTypes.BEER_DELETED;
import static com.study.spring6restmvc.listeners.BeerEventTypes.BEER_PATCHED;
import static com.study.spring6restmvc.listeners.BeerEventTypes.BEER_UPDATED;

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
            case BeerCreatedEvent ignored -> auditEventType = BEER_CREATED.name();
            case BeerUpdatedEvent ignored -> auditEventType = BEER_UPDATED.name();
            case BeerDeletedEvent ignored -> auditEventType = BEER_DELETED.name();
            case BeerPatchedEvent ignored -> auditEventType = BEER_PATCHED.name();
            default -> auditEventType = "UNKNOWN";
        }

        var beerAudit = beerMapper.beerToBeerAudit(event.getBeer());
        beerAudit.setAuditEventType(auditEventType);

        if (event.getAuthentication() != null && event.getAuthentication().getName() != null) {
            beerAudit.setAuditPrincipalName(event.getAuthentication().getName());
        }

        var savedBeerAudit = beerAuditRepository.save(beerAudit);
        log.debug("{} Audit saved: id {}", auditEventType, savedBeerAudit.getAuditId());
    }
}
