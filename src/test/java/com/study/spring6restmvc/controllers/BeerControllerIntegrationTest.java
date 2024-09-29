package com.study.spring6restmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.spring6restmvc.config.JwtDecoderConfig;
import com.study.spring6restmvc.entities.Beer;
import com.study.spring6restmvc.events.beer.BeerCreatedEvent;
import com.study.spring6restmvc.events.beer.BeerDeletedEvent;
import com.study.spring6restmvc.events.beer.BeerPatchedEvent;
import com.study.spring6restmvc.events.beer.BeerUpdatedEvent;
import com.study.spring6restmvc.exceptions.NotFoundException;
import com.study.spring6restmvc.mappers.BeerMapper;
import com.study.spring6restmvc.repositories.BeerRepository;
import com.study.spring6restmvcapi.model.BeerDTO;
import com.study.spring6restmvcapi.model.BeerStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

import static com.study.spring6restmvc.controllers.BeerController.BEER_ID_PATH;
import static com.study.spring6restmvc.controllers.BeerController.BEER_PATH;
import static com.study.spring6restmvc.util.TestUtils.AUTH_HEADER_GENERATED_VALUE;
import static com.study.spring6restmvc.util.TestUtils.AUTH_HEADER_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RecordApplicationEvents
@Import(JwtDecoderConfig.class)
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
    @Autowired
    private ApplicationEvents applicationEvents;

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
        var beerDTOPagedModel = beerController.getAllBeers(null, null, false, 1, 2400);

        assertThat(beerDTOPagedModel).isNotNull();
        assertThat(beerDTOPagedModel.getContent()).hasSize(1000);
    }

    @Test
    @Transactional
    void testGetAllBeersReturnsEmptyListIfNoBeers() {
        beerRepository.deleteAll();

        var beerDTOPagedModel = beerController.getAllBeers(null, null, false, 1, 25);

        assertThat(beerDTOPagedModel).isNotNull();
        assertThat(beerDTOPagedModel.getContent()).isEmpty();
    }

    @Test
    void testGetAllBeersWithQueryParamBeerName() throws Exception {
        mockMvc.perform(
                        get(BEER_PATH)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE)
                                .queryParam("beerName", BeerStyle.IPA.name())
                                .queryParam("pageSize", "1000"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content.size()", greaterThan(300)));
    }

    @Test
    void testGetAllBeersWithQueryParamBeerStyle() throws Exception {
        mockMvc.perform(
                        get(BEER_PATH)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE)
                                .queryParam("beerStyle", BeerStyle.IPA.name()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content.size()", greaterThan(10)));
    }

    @Test
    void testGetAllBeersWithQueryParamBeerNameAndBeerStyle() throws Exception {
        mockMvc.perform(
                        get(BEER_PATH)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE)
                                .queryParam("beerName", "India")
                                .queryParam("beerStyle", "IPA")
                                .queryParam("pageSize", "1000"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content.size()", greaterThan(40)));
    }

    @Test
    void testGetAllBeersWithQueryParamBeerNameAndBeerStyleShowInventory() throws Exception {
        mockMvc.perform(
                        get(BEER_PATH)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE)
                                .queryParam("beerName", "IPA")
                                .queryParam("beerStyle", BeerStyle.IPA.name())
                                .queryParam("showInventory", "false")
                                .queryParam("pageSize", "1000"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content.size()", greaterThan(300)),
                        jsonPath("$.content.[0].quantityOnHand").value(nullValue()));
    }

    @Test
    void testGetAllBeersWithQueryParamBeerNameAndBeerStyleShowInventoryTruePageTwo() throws Exception {
        mockMvc.perform(
                        get(BEER_PATH)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE)
                                .queryParam("beerName", "IPA")
                                .queryParam("beerStyle", BeerStyle.IPA.name())
                                .queryParam("showInventory", "true")
                                .queryParam("pageNumber", "2")
                                .queryParam("pageSize", "50"))
                .andExpectAll(
                        status().isOk(),
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
        var beerDto = beerController.getBeerById(testBeer.getId());

        assertThat(beerDto).isNotNull();
    }

    @Test
    void testGetBeerByIdThrowsNotFoundExceptionIfBeerDoesNotExist() {
        var randomUuid = UUID.randomUUID();

        assertThrows(NotFoundException.class,
                () -> beerController.getBeerById(randomUuid));
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
    @Transactional
    void testCreateBeerMvc() throws Exception {
        var beerDto = BeerDTO.builder()
                .beerName("Beer Name")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("123")
                .price(new BigDecimal("11.11"))
                .quantityOnHand(5)
                .build();

        mockMvc.perform(
                        post(BEER_PATH)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION));

        assertThat(applicationEvents.stream(BeerCreatedEvent.class).count())
                .isEqualTo(1);
    }

    @Test
    void testUpdateBeerThrowsNotFoundExceptionIfBeerDoesNotExist() {
        var randomUuid = UUID.randomUUID();
        var beerDto = BeerDTO.builder().build();

        assertThrows(NotFoundException.class,
                () -> beerController.updateBeerById(randomUuid, beerDto));
    }

    @Test
    @Transactional
    void testUpdateBeer() {
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
    void testUpdateBeerMVC() throws Exception {
        var beerDto = beerMapper.beerToBeerDTO(testBeer);
        beerDto.setBeerName("updatedBeer");

        mockMvc.perform(
                        put(BEER_ID_PATH, testBeer.getId())
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerDto)))
                .andExpect(status().isNoContent());


        assertThat(testBeer.getBeerName()).isEqualTo(beerDto.getBeerName());
        assertThat(applicationEvents.stream(BeerUpdatedEvent.class).count())
                .isEqualTo(1);
    }

    @Test
    @Transactional
    void testDeleteBeerById() {
        var beerId = testBeer.getId();

        var responseEntity = beerController.deleteBeerById(beerId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(beerRepository.findById(beerId)).isEmpty();
    }

    @Test
    @Transactional
    void testDeleteBeerMVC() throws Exception {
        var beerId = testBeer.getId();

        mockMvc.perform(
                        delete(BEER_ID_PATH, beerId)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE))
                .andExpect(
                        status().isNoContent());

        assertThat(beerRepository.findById(beerId)).isEmpty();
        assertThat(applicationEvents.stream(BeerDeletedEvent.class).count()).isEqualTo(1);
    }

    @Test
    void testDeleteByIdThrowsNotFoundExceptionIfBeerDoesNotExist() {
        var randomUuid = UUID.randomUUID();

        assertThrows(NotFoundException.class,
                () -> beerController.deleteBeerById(randomUuid));
    }

    @Test
    @Transactional
    void testPatchBeerById() {
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
    @Transactional
    void testPatchBeerMVC() throws Exception {
        var beerDto = beerMapper.beerToBeerDTO(testBeer);
        beerDto.setBeerName("patchedBeer");

        mockMvc.perform(
                        patch(BEER_ID_PATH, testBeer.getId())
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerDto)))
                .andExpect(status().isNoContent());


        assertThat(testBeer.getBeerName()).isEqualTo(beerDto.getBeerName());
        assertThat(applicationEvents.stream(BeerPatchedEvent.class).count())
                .isEqualTo(1);
    }

    @Test
    void testPatchBeerByIdWithTooLongNameReturnsBadRequest() throws Exception {
        var jsonMap = new HashMap<String, String>();
        String tooLongName = "Beer Name 012345678901234567890123456789012345678901234567890123456789012345678901234567890";
        jsonMap.put("beerName", tooLongName);


        mockMvc.perform(
                        patch(BEER_ID_PATH, testBeer.getId())
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(jsonMap)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.length()", is(1)));
    }

    @Test
    void testGetAllBeersWithNoAuthReturnsUnauthorized() throws Exception {
        mockMvc.perform(
                        get(BEER_PATH)
                                .queryParam("beerName", BeerStyle.IPA.name())
                                .queryParam("pageSize", "1000"))
                .andExpect(
                        status().isUnauthorized());
    }
}
