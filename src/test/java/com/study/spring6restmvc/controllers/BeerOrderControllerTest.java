package com.study.spring6restmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.spring6restmvc.config.JwtDecoderMockConfig;
import com.study.spring6restmvc.model.BeerOrderDTO;
import com.study.spring6restmvc.model.CustomerDTO;
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

import java.util.UUID;

import static com.study.spring6restmvc.controllers.BeerOrderController.BEER_ORDER_ID_PATH;
import static com.study.spring6restmvc.controllers.BeerOrderController.BEER_ORDER_PATH;
import static com.study.spring6restmvc.util.TestUtils.AUTH_HEADER_GENERATED_VALUE;
import static com.study.spring6restmvc.util.TestUtils.AUTH_HEADER_KEY;
import static com.study.spring6restmvc.util.TestUtils.AUTH_HEADER_MOCK_VALUE;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    @Captor
    private ArgumentCaptor<CustomerDTO> customerArgumentCaptor;

    private BeerOrderServiceImpl beerOrderServiceImpl;
    private BeerOrderDTO testBeerOrderDTO;

    @BeforeEach
    void setUp() {
        beerOrderServiceImpl = new BeerOrderServiceImpl();
        testBeerOrderDTO = beerOrderServiceImpl.getAll(null, null).getContent().getFirst();
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
        var beerOrderDtoId = testBeerOrderDTO.getId();

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
}