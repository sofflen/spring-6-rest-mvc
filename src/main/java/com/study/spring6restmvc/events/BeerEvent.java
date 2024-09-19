package com.study.spring6restmvc.events;

import com.study.spring6restmvc.entities.Beer;
import org.springframework.security.core.Authentication;

public interface BeerEvent {

    Beer getBeer();

    Authentication getAuthentication();

}
