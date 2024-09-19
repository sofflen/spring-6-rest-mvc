package com.study.spring6restmvc.listeners;

import com.study.spring6restmvc.events.BeerCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BeerCreatedListener {

    @EventListener
    @Async
    public void listen(BeerCreatedEvent event) {
        log.info("Beer created, id: {}", event.getBeer().getId());
        //todo add real implementation to persist audit record
    }
}
