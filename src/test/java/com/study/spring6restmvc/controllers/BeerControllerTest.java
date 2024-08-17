package com.study.spring6restmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.spring6restmvc.model.Beer;
import com.study.spring6restmvc.services.BeerService;
import com.study.spring6restmvc.services.BeerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BeerService beerService;

    private BeerServiceImpl beerServiceImpl;

    @BeforeEach
    void setUp() {
        beerServiceImpl = new BeerServiceImpl();
    }

    @Test
    void createBeer() throws Exception {
        Beer testBeer = Beer.builder().build();

        given(beerService.saveBeer(any(Beer.class))).willReturn(beerServiceImpl.getAllBeers().getFirst());

        mockMvc.perform(post("/api/v1/beer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBeer)))
                .andExpectAll(status().isCreated(),
                        header().exists("Location"),
                        redirectedUrl("/api/v1/beer/" + beerServiceImpl.getAllBeers().getFirst().getId()));
    }

    @Test
    void GetAllBeers() throws Exception {
        given(beerService.getAllBeers()).willReturn(beerServiceImpl.getAllBeers());

        mockMvc.perform(get("/api/v1/beer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(3)));
    }

    @Test
    void GetBeerById() throws Exception {
        Beer testBeer = beerServiceImpl.getAllBeers().getFirst();
        UUID beerId = testBeer.getId();

        given(beerService.getBeerById(beerId)).willReturn(testBeer);

        mockMvc.perform(get("/api/v1/beer/" + beerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testBeer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(testBeer.getBeerName())));

    }
}