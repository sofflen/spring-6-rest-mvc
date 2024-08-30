package com.study.spring6restmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.spring6restmvc.entities.Beer;
import com.study.spring6restmvc.exceptions.NotFoundException;
import com.study.spring6restmvc.mappers.BeerMapper;
import com.study.spring6restmvc.model.BeerDTO;
import com.study.spring6restmvc.model.BeerStyle;
import com.study.spring6restmvc.repositories.BeerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.UUID;

import static com.study.spring6restmvc.controllers.BeerController.BEER_PATH;
import static com.study.spring6restmvc.controllers.BeerController.BEER_PATH_ID;
import static com.study.spring6restmvc.util.TestUtils.AUTH_PASSWORD;
import static com.study.spring6restmvc.util.TestUtils.AUTH_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerControllerIntegrationTest {

    @Autowired
    private BeerController beerController;
    @Autowired
    private BeerRepository beerRepository;
    @Autowired
    private BeerMapper beerMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext wac;

    private Beer testBeer;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        testBeer = beerRepository.findAll().getFirst();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    void testGetAllBeers() {
        var beerDtoPage = beerController.getAllBeers(null, null, false, 1, 2400);

        assertThat(beerDtoPage).isNotNull();
        assertThat(beerDtoPage.getContent().size()).isEqualTo(1000);
    }

    @Test
    @Transactional
    void testGetAllBeersReturnsEmptyListIfNoBeers() {
        beerRepository.deleteAll();

        var beerDtoPage = beerController.getAllBeers(null, null, false, 1, 25);

        assertThat(beerDtoPage).isNotNull();
        assertThat(beerDtoPage.isEmpty()).isTrue();
    }

    @Test
    void testGetAllBeersWithQueryParamBeerName() throws Exception {
        mockMvc.perform(get(BEER_PATH)
                        .with(httpBasic(AUTH_USERNAME, AUTH_PASSWORD))
                        .queryParam("beerName", BeerStyle.IPA.name())
                        .queryParam("pageSize", "1000"))
                .andExpectAll(status().isOk(),
                        jsonPath("$.content.size()", greaterThan(300)));
    }

    @Test
    void testGetAllBeersWithQueryParamBeerStyle() throws Exception {
        mockMvc.perform(get(BEER_PATH)
                        .with(httpBasic(AUTH_USERNAME, AUTH_PASSWORD))
                        .queryParam("beerStyle", BeerStyle.IPA.name()))
                .andExpectAll(status().isOk(),
                        jsonPath("$.size()", greaterThan(10)));
    }

    @Test
    void testGetAllBeersWithQueryParamBeerNameAndBeerStyle() throws Exception {
        mockMvc.perform(get(BEER_PATH)
                        .with(httpBasic(AUTH_USERNAME, AUTH_PASSWORD))
                        .queryParam("beerName", "India")
                        .queryParam("beerStyle", "IPA")
                        .queryParam("pageSize", "1000"))
                .andExpectAll(status().isOk(),
                        jsonPath("$.content.size()", greaterThan(40)));
    }

    @Test
    void testGetAllBeersWithQueryParamBeerNameAndBeerStyleShowInventory() throws Exception {
        mockMvc.perform(get(BEER_PATH)
                        .with(httpBasic(AUTH_USERNAME, AUTH_PASSWORD))
                        .queryParam("beerName", "IPA")
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("showInventory", "false")
                        .queryParam("pageSize", "1000"))
                .andExpectAll(status().isOk(),
                        jsonPath("$.content.size()", greaterThan(300)),
                        jsonPath("$.content.[0].quantityOnHand").value(nullValue()));
    }

    @Test
    void testGetAllBeersWithQueryParamBeerNameAndBeerStyleShowInventoryTruePageTwo() throws Exception {
        mockMvc.perform(get(BEER_PATH)
                        .with(httpBasic(AUTH_USERNAME, AUTH_PASSWORD))
                        .queryParam("beerName", "IPA")
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("showInventory", "true")
                        .queryParam("pageNumber", "2")
                        .queryParam("pageSize", "50"))
                .andExpectAll(status().isOk(),
                        jsonPath("$.content.size()", is(50)),
                        jsonPath("$.content.[0].quantityOnHand").value(notNullValue()));
    }

    @Test
    void testGetAllBeersWithBadQueryParamsThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> beerController
                .getAllBeers("bad beer name", BeerStyle.IPA, false, 1, 25));
    }

    @Test
    void testGetBeerById() {
        var testBeer = beerRepository.findAll().getFirst();
        var beerDto = beerController.getBeerById(testBeer.getId());

        assertThat(beerDto).isNotNull();
    }

    @Test
    void testGetBeerByIdThrowsNotFoundExceptionIfBeerDoesNotExist() {
        assertThrows(NotFoundException.class,
                () -> beerController.getBeerById(UUID.randomUUID()));
    }

    @Test
    @Transactional
    void testCreateBeer() {
        var beerDto = BeerDTO.builder()
                .beerName("New Beer")
                .build();

        var responseEntity = beerController.createBeer(beerDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        var locationSplitStrArray = responseEntity.getHeaders().getLocation().toString().split("/");
        var beerId = UUID.fromString(locationSplitStrArray[locationSplitStrArray.length - 1]);

        assertThat(beerRepository.findById(beerId)).isNotNull();
    }

    @Test
    void testUpdateBeerThrowsNotFoundExceptionIfBeerDoesNotExist() {
        assertThrows(NotFoundException.class,
                () -> beerController.updateBeerById(UUID.randomUUID(), BeerDTO.builder().build()));
    }

    @Test
    @Transactional
    void testUpdateBeer() {
        var testBeer = beerRepository.findAll().getFirst();
        var beerDto = beerMapper.beerToBeerDTO(testBeer);
        final String newBeerName = "New Beer";

        beerDto.setId(null);
        beerDto.setVersion(null);
        beerDto.setBeerName(newBeerName);

        var responseEntity = beerController.updateBeerById(testBeer.getId(), beerDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        var updatedBeer = beerRepository.findById(testBeer.getId()).orElseThrow();

        assertThat(updatedBeer.getBeerName()).isEqualTo(newBeerName);
    }

    @Test
    @Transactional
    void testDeleteBeerById() {
        var beerId = testBeer.getId();

        var ResponseEntity = beerController.deleteBeerById(beerId);

        assertThat(ResponseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(beerRepository.findById(beerId).isEmpty()).isTrue();
    }

    @Test
    void testDeleteByIdThrowsNotFoundExceptionIfBeerDoesNotExist() {
        assertThrows(NotFoundException.class,
                () -> beerController.deleteBeerById(UUID.randomUUID()));
    }

    @Test
    @Transactional
    void testPatchBeerById() {
        var testBeer = beerRepository.findAll().getFirst();
        var beerDto = beerMapper.beerToBeerDTO(testBeer);
        final String newBeerName = "New Beer";

        beerDto.setId(null);
        beerDto.setVersion(null);
        beerDto.setBeerName(newBeerName);

        var responseEntity = beerController.patchBeerById(testBeer.getId(), beerDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        var updatedBeer = beerRepository.findById(testBeer.getId()).orElseThrow();

        assertThat(updatedBeer.getBeerName()).isEqualTo(newBeerName);
    }

    @Test
    void testPatchBeerByIdWithTooLongNameReturnsBadRequest() throws Exception {
        var jsonMap = new HashMap<String, String>();
        String tooLongName = "Beer Name 012345678901234567890123456789012345678901234567890123456789012345678901234567890";
        jsonMap.put("beerName", tooLongName);


        mockMvc.perform(patch(BEER_PATH_ID, testBeer.getId())
                        .with(httpBasic(AUTH_USERNAME, AUTH_PASSWORD))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonMap)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void testGetAllBeersWithNoAuthReturnsUnauthorized() throws Exception {
        mockMvc.perform(get(BEER_PATH)
                        .queryParam("beerName", BeerStyle.IPA.name())
                        .queryParam("pageSize", "1000"))
                .andExpect(status().isUnauthorized());
    }
}
