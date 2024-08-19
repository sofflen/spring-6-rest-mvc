package com.study.spring6restmvc.services;

import com.study.spring6restmvc.mappers.BeerMapper;
import com.study.spring6restmvc.model.BeerDTO;
import com.study.spring6restmvc.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.util.StringUtils.hasText;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Override
    public Optional<BeerDTO> getBeerById(UUID beerId) {
        return Optional.ofNullable(
                beerMapper.beerToBeerDTO(
                        beerRepository.findById(beerId).orElse(null)));
    }

    @Override
    public List<BeerDTO> getAllBeers() {
        return beerRepository.findAll()
                .stream()
                .map(beerMapper::beerToBeerDTO)
                .toList();
    }

    @Override
    public BeerDTO saveBeer(BeerDTO beer) {
        return beerMapper.beerToBeerDTO(
                beerRepository.save(
                        beerMapper.beerDtoToBeer(beer)));
    }

    @Override
    public Optional<BeerDTO> updateBeerById(UUID beerId, BeerDTO beer) {
        AtomicReference<BeerDTO> atomicReference = new AtomicReference<>();

        beerRepository.findById(beerId).ifPresentOrElse(
                foundBeer -> {
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
}
