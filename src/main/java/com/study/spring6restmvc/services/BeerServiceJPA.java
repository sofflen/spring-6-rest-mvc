package com.study.spring6restmvc.services;

import com.study.spring6restmvc.entities.Beer;
import com.study.spring6restmvc.events.BeerCreatedEvent;
import com.study.spring6restmvc.mappers.BeerMapper;
import com.study.spring6restmvc.model.BeerDTO;
import com.study.spring6restmvc.model.BeerStyle;
import com.study.spring6restmvc.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.util.StringUtils.hasText;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class BeerServiceJPA implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;
    private final CacheManager cacheManager;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Cacheable(cacheNames = "beerCache", key = "#beerId")
    public Optional<BeerDTO> getBeerById(UUID beerId) {
        log.info("BeerService: getBeerById({})", beerId);
        return Optional.ofNullable(
                beerMapper.beerToBeerDTO(
                        beerRepository.findById(beerId).orElse(null)));
    }

    @Override
    @Cacheable(cacheNames = "beerListCache", condition = "(#beerName != null && #beerStyle == null && #showInventory == null && #pageNumber == null && #pageSize == null) || (#beerName == null && #beerStyle == null && #showInventory == null && #pageNumber == null && #pageSize == null)")
    public Page<BeerDTO> getAllBeers(String beerName, BeerStyle beerStyle, Boolean showInventory,
                                     Integer pageNumber, Integer pageSize) {
        log.info("BeerService: getAllBeers()");

        PageRequest pageRequest = ServiceUtils.buildPageRequest(pageNumber, pageSize, Sort.by("beerName"));
        Page<Beer> beerPage;

        if (hasText(beerName) && beerStyle != null) {
            beerPage = getBeersByNameAndStyle(beerName, beerStyle, pageRequest);
        } else if (hasText(beerName)) {
            beerPage = getBeersByName(beerName, pageRequest);
        } else if (beerStyle != null) {
            beerPage = getBeersByStyle(beerStyle, pageRequest);
        } else {
            beerPage = beerRepository.findAll(pageRequest);
        }

        if (showInventory != null && !showInventory) {
            beerPage.forEach(beer -> beer.setQuantityOnHand(null));
        }

        return beerPage.map(beerMapper::beerToBeerDTO);
    }

    @Override
    public BeerDTO saveBeer(BeerDTO beer) {
        clearCache(null);

        var mappedBeer = beerMapper.beerDtoToBeer(beer);
        var savedBeer = beerRepository.save(mappedBeer);
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        eventPublisher.publishEvent(new BeerCreatedEvent(savedBeer, authentication));

        return beerMapper.beerToBeerDTO(savedBeer);
    }

    @Override
    public Optional<BeerDTO> updateBeerById(UUID beerId, BeerDTO beer) {
        AtomicReference<BeerDTO> atomicReference = new AtomicReference<>();

        beerRepository.findById(beerId).ifPresentOrElse(
                foundBeer -> {
                    clearCache(beerId);

                    foundBeer.setBeerName(beer.getBeerName());
                    foundBeer.setBeerStyle(beer.getBeerStyle());
                    foundBeer.setUpc(beer.getUpc());
                    foundBeer.setPrice(beer.getPrice());
                    foundBeer.setQuantityOnHand(beer.getQuantityOnHand());
                    foundBeer.setUpdatedAt(LocalDateTime.now());

                    atomicReference.set(
                            beerMapper.beerToBeerDTO(
                                    beerRepository.save(foundBeer)));
                },
                () -> atomicReference.set(null));

        return Optional.ofNullable(atomicReference.get());
    }

    @Override
    public boolean deleteBeerById(UUID beerId) {
        if (beerRepository.existsById(beerId)) {
            clearCache(beerId);
            beerRepository.deleteById(beerId);
            return true;
        }
        return false;
    }

    @Override
    public Optional<BeerDTO> patchBeerById(UUID beerId, BeerDTO beer) {
        AtomicReference<BeerDTO> atomicReference = new AtomicReference<>();

        beerRepository.findById(beerId).ifPresentOrElse(
                foundBeer -> {
                    clearCache(beerId);

                    if (hasText(beer.getBeerName()))
                        foundBeer.setBeerName(beer.getBeerName());
                    if (beer.getBeerStyle() != null)
                        foundBeer.setBeerStyle(beer.getBeerStyle());
                    if (hasText(beer.getUpc()))
                        foundBeer.setUpc(beer.getUpc());
                    if (beer.getPrice() != null
                            && beer.getPrice().compareTo(new BigDecimal("0.00")) > 0)
                        foundBeer.setPrice(beer.getPrice());
                    if (beer.getQuantityOnHand() != null)
                        foundBeer.setQuantityOnHand(beer.getQuantityOnHand());
                    if (beer.getUpdatedAt() != null)
                        foundBeer.setUpdatedAt(LocalDateTime.now());

                    atomicReference.set(
                            beerMapper.beerToBeerDTO(
                                    beerRepository.save(foundBeer)));
                },
                () -> atomicReference.set(null));

        return Optional.ofNullable(atomicReference.get());
    }

    private Page<Beer> getBeersByName(String beerName, Pageable pageable) {
        return beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + beerName + "%", pageable);
    }

    private Page<Beer> getBeersByStyle(BeerStyle beerStyle, Pageable pageable) {
        return beerRepository.findAllByBeerStyle(beerStyle, pageable);
    }

    private Page<Beer> getBeersByNameAndStyle(String beerName, BeerStyle beerStyle, Pageable pageable) {
        return beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle(
                "%" + beerName + "%", beerStyle, pageable);
    }

    private void clearCache(UUID beerId) {
        Cache beerListCache = cacheManager.getCache("beerListCache");

        if (beerListCache != null) {
            beerListCache.clear();
        }

        if (beerId != null) {
            Cache beerCache = cacheManager.getCache("beerCache");

            if (beerCache != null) {
                beerCache.evict(beerId);
            }
        }
    }
}
