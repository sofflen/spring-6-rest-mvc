package com.study.spring6restmvc.events;

import com.study.spring6restmvc.entities.Beer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BeerCreatedEvent {
    private Beer beer;
    private Authentication authentication;
}
