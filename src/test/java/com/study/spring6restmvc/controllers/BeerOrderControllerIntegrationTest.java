package com.study.spring6restmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.spring6restmvc.config.JwtDecoderConfig;
import com.study.spring6restmvc.entities.BeerOrder;
import com.study.spring6restmvc.exceptions.NotFoundException;
import com.study.spring6restmvc.mappers.BeerOrderMapper;
import com.study.spring6restmvc.model.BeerOrderCreateDTO;
import com.study.spring6restmvc.repositories.BeerOrderRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static com.study.spring6restmvc.controllers.BeerOrderController.BEER_ORDER_ID_PATH;
import static com.study.spring6restmvc.controllers.BeerOrderController.BEER_ORDER_PATH;
import static com.study.spring6restmvc.util.TestUtils.AUTH_HEADER_GENERATED_VALUE;
import static com.study.spring6restmvc.util.TestUtils.AUTH_HEADER_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(JwtDecoderConfig.class)
class BeerOrderControllerIntegrationTest {

    @Autowired
    private BeerOrderController beerOrderController;
    @Autowired
    private BeerOrderRepository beerOrderRepository;
    @Autowired
    private BeerOrderMapper beerOrderMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext wac;

    private BeerOrder testBeerOrder;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        testBeerOrder = beerOrderRepository.findAll().getFirst();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    void testGetAllBeerOrders() {
        var beerOrderPagedModel = beerOrderController.getAllBeerOrders(1, 25);
        assertThat(beerOrderPagedModel).isNotNull();
        assertThat(beerOrderPagedModel.getContent()).isNotEmpty();
    }

    @Test
    void testGetAllBeerOrdersMVC() throws Exception {
        mockMvc.perform(
                        get(BEER_ORDER_PATH)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content.length()", Matchers.greaterThan(0)));
    }

    @Test
    void testGetBeerById() {
        var beerOrderDto = beerOrderController.getBeerOrderById(testBeerOrder.getId());

        assertThat(beerOrderDto).isNotNull();
    }

    @Test
    void testGetBeerByIdThrowsNotFoundExceptionIfBeerDoesNotExist() {
        var randomUuid = UUID.randomUUID();

        assertThrows(NotFoundException.class,
                () -> beerOrderController.getBeerOrderById(randomUuid));
    }

    @Test
    void testGetBeerOrderByIdMvc() throws Exception {
        var beerOrderId = testBeerOrder.getId();

        mockMvc.perform(
                        get(BEER_ORDER_ID_PATH, beerOrderId)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id", is(beerOrderId.toString())));
    }

    @Test
    @Transactional
    void testCreateBeerOrder() {
        var beerOrderCreateDto = BeerOrderCreateDTO.builder()
                .customerId(testBeerOrder.getCustomer().getId())
                .build();

        var responseEntity = beerOrderController.createBeerOrder(beerOrderCreateDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        var locationSplitStrArray = responseEntity.getHeaders().getLocation().toString().split("/");
        var beerOrderId = UUID.fromString(locationSplitStrArray[locationSplitStrArray.length - 1]);

        assertThat(beerOrderRepository.findById(beerOrderId)).isNotNull();
    }

    @Test
    void testCreateBeerOrderThrowsNotFoundExceptionIfCustomerDoesNotExist() {
        var beerOrderCreateDto = BeerOrderCreateDTO.builder()
                .customerId(UUID.randomUUID())
                .build();

        assertThrows(NotFoundException.class, () -> beerOrderController.createBeerOrder(beerOrderCreateDto));
    }

    @Test
    @Transactional
    void testCreateBeerOrderMvc() throws Exception {
        var beerOrderCreateDto = BeerOrderCreateDTO.builder()
                .customerId(testBeerOrder.getCustomer().getId())
                .build();

        mockMvc.perform(
                        post(BEER_ORDER_PATH)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerOrderCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }
}
