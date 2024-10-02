package com.study.spring6restmvc.listeners;

import com.study.spring6restmvcapi.events.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BeerOrderEventListener {

    @Async
    @EventListener
    public void listen(OrderPlacedEvent orderPlacedEvent) {
        //todo add Kafka
        log.info("OrderPlacedEvent: {}", orderPlacedEvent);
    }
}
