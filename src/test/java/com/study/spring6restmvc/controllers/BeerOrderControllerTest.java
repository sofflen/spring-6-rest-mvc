package com.study.spring6restmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.spring6restmvc.config.JwtDecoderMockConfig;
import com.study.spring6restmvc.model.BeerOrderDTO;
import com.study.spring6restmvc.model.BeerOrderRequestBodyDTO;
import com.study.spring6restmvc.model.BeerOrderShipmentDTO;
import com.study.spring6restmvc.services.BeerOrderService;
import com.study.spring6restmvc.services.BeerOrderServiceImpl;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static com.study.spring6restmvc.controllers.BeerOrderController.BEER_ORDER_ID_PATH;
import static com.study.spring6restmvc.controllers.BeerOrderController.BEER_ORDER_PATH;
import static com.study.spring6restmvc.util.TestUtils.AUTH_HEADER_GENERATED_VALUE;
import static com.study.spring6restmvc.util.TestUtils.AUTH_HEADER_KEY;
import static com.study.spring6restmvc.util.TestUtils.AUTH_HEADER_MOCK_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BeerOrderController.class)
@Import(JwtDecoderMockConfig.class)
class BeerOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BeerOrderService beerOrderService;

    @Captor
    private ArgumentCaptor<UUID> uuidArgumentCaptor;

    private BeerOrderServiceImpl beerOrderServiceImpl;
    private BeerOrderDTO testBeerOrderDto;

    @BeforeEach
    void setUp() {
        beerOrderServiceImpl = new BeerOrderServiceImpl();
        testBeerOrderDto = beerOrderServiceImpl.getAll(null, null).getContent().getFirst();
    }

    @Test
    void testGetAllBeerOrders() throws Exception {
        given(beerOrderService.getAll(any(), any())).willReturn(beerOrderServiceImpl.getAll(null, null));

        mockMvc.perform(
                        get(BEER_ORDER_PATH)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content.length()", Matchers.greaterThan(1)));

        verify(beerOrderService).getAll(null, null);
    }

    @Test
    void testGetBeerOrderById() throws Exception {
        var beerOrderDtoId = testBeerOrderDto.getId();

        given(beerOrderService.getById(any())).willReturn(beerOrderServiceImpl.getById(beerOrderDtoId));

        mockMvc.perform(
                        get(BEER_ORDER_ID_PATH, beerOrderDtoId)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id", is(beerOrderDtoId.toString())));

        verify(beerOrderService).getById(beerOrderDtoId);
    }

    @Test
    void testGetBeerOrderByIdNotFound() throws Exception {
        var optionalBeerOrderDto = beerOrderServiceImpl.getById(UUID.randomUUID());

        given(beerOrderService.getById(any(UUID.class))).willReturn(optionalBeerOrderDto);

        mockMvc.perform(
                        get(BEER_ORDER_ID_PATH, UUID.randomUUID())
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE))
                .andExpect(
                        status().isNotFound());

        verify(beerOrderService).getById(any(UUID.class));
    }

    @Test
    void testCreateBeerOrder() throws Exception {
        var beerOrderCreateDto = BeerOrderRequestBodyDTO.builder()
                .customerId(UUID.randomUUID())
                .build();

        given(beerOrderService.save(any(BeerOrderRequestBodyDTO.class))).willReturn(testBeerOrderDto);

        mockMvc.perform(
                        post(BEER_ORDER_PATH)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerOrderCreateDto)))
                .andExpectAll(
                        status().isCreated(),
                        header().exists("Location"),
                        redirectedUrlTemplate(BEER_ORDER_ID_PATH, testBeerOrderDto.getId()));

        verify(beerOrderService).save(any(BeerOrderRequestBodyDTO.class));
    }

    @Test
    void updateBeerById() throws Exception {
        var testBeerOrderUpdateDto = BeerOrderRequestBodyDTO.builder()
                .customerId(UUID.randomUUID())
                .beerOrderShipment(BeerOrderShipmentDTO.builder().trackingNumber("trackingNumber").build())
                .customerRef("customer ref")
                .build();

        given(beerOrderService.updateById(any(UUID.class), any(BeerOrderRequestBodyDTO.class))).willReturn(Optional.of(testBeerOrderDto));

        mockMvc.perform(
                        put(BEER_ORDER_ID_PATH, testBeerOrderDto.getId())
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testBeerOrderUpdateDto)))
                .andExpect(
                        status().isNoContent());

        verify(beerOrderService).updateById(uuidArgumentCaptor.capture(), eq(testBeerOrderUpdateDto));

        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(testBeerOrderDto.getId());
    }
}