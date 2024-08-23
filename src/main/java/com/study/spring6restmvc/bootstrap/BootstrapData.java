package com.study.spring6restmvc.bootstrap;

import com.study.spring6restmvc.entities.Beer;
import com.study.spring6restmvc.entities.Customer;
import com.study.spring6restmvc.model.BeerCsvRecord;
import com.study.spring6restmvc.model.BeerStyle;
import com.study.spring6restmvc.model.CustomerCsvRecord;
import com.study.spring6restmvc.repositories.BeerRepository;
import com.study.spring6restmvc.repositories.CustomerRepository;
import com.study.spring6restmvc.services.CsvService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;
    private final CsvService<BeerCsvRecord> beerCsvService;
    private final CsvService<CustomerCsvRecord> customerCsvService;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        loadBeerData();
        loadCustomerData();
        loadBeerCsvData();
        loadCustomerCsvData();
    }

    private void loadBeerData() {
        if (beerRepository.count() == 0) {
            Beer beer1 = Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle(BeerStyle.PALE_ALE)
                    .upc("12356")
                    .price(new BigDecimal("12.99"))
                    .quantityOnHand(122)
                    .build();
            Beer beer2 = Beer.builder()
                    .beerName("Crank")
                    .beerStyle(BeerStyle.PALE_ALE)
                    .upc("12356222")
                    .price(new BigDecimal("11.99"))
                    .quantityOnHand(392)
                    .build();
            Beer beer3 = Beer.builder()
                    .beerName("Sunshine City")
                    .beerStyle(BeerStyle.IPA)
                    .upc("12356")
                    .price(new BigDecimal("13.99"))
                    .quantityOnHand(144)
                    .build();

            beerRepository.saveAll(List.of(beer1, beer2, beer3));
        }
    }

    private void loadCustomerData() {
        if (customerRepository.count() == 0) {
            Customer customer1 = Customer.builder()
                    .customerName("John Doe")
                    .email("john.doe@gmail.com")
                    .build();
            Customer customer2 = Customer.builder()
                    .customerName("Jane Doe")
                    .email("jane.doe@gmail.com")
                    .build();
            Customer customer3 = Customer.builder()
                    .customerName("Thomas Doe")
                    .email("thomas.doe@gmail.com")
                    .build();

            customerRepository.saveAll(List.of(customer1, customer2, customer3));
        }
    }

    private void loadBeerCsvData() throws FileNotFoundException {
        if (beerRepository.count() < 10) {
            File csvFile = ResourceUtils.getFile("classpath:csvdata/beers.csv");

            var csvBeersList = beerCsvService.convertCSVToList(csvFile);

            csvBeersList.forEach(beerCsvRecord -> {
                BeerStyle beerStyle = switch (beerCsvRecord.getStyle()) {
                    case "American Pale Lager" -> BeerStyle.LAGER;
                    case "American Pale Ale (APA)", "American Black Ale", "Belgian Dark Ale", "American Blonde Ale" ->
                            BeerStyle.ALE;
                    case "American IPA", "American Double / Imperial IPA", "Belgian IPA" -> BeerStyle.IPA;
                    case "American Porter" -> BeerStyle.PORTER;
                    case "Oatmeal Stout", "American Stout" -> BeerStyle.STOUT;
                    case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
                    case "Fruit / Vegetable Beer", "Winter Warmer", "Berliner Weissbier" -> BeerStyle.WHEAT;
                    case "English Pale Ale" -> BeerStyle.PALE_ALE;
                    default -> BeerStyle.PILSNER;
                };

                beerRepository.save(Beer.builder()
                        .beerName(StringUtils.abbreviate(beerCsvRecord.getBeer(), 50))
                        .beerStyle(beerStyle)
                        .upc(beerCsvRecord.getId().toString())
                        .price(BigDecimal.valueOf(new SecureRandom().nextDouble(8.0, 18.0)))
                        .quantityOnHand(beerCsvRecord.getQuantity())
                        .build());
            });
        }
    }

    private void loadCustomerCsvData() throws FileNotFoundException {
        if (customerRepository.count() < 10) {
            File csvFile = ResourceUtils.getFile("classpath:csvdata/customers.csv");

            var csvCustomersList = customerCsvService.convertCSVToList(csvFile);

            csvCustomersList.forEach(customerCsvRecord -> customerRepository
                    .save(Customer.builder()
                            .customerName(customerCsvRecord.getName())
                            .email(customerCsvRecord.getEmail())
                            .build()));
        }
    }
}
