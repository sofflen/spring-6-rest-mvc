package com.study.spring6restmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.spring6restmvc.config.JwtDecoderMockConfig;
import com.study.spring6restmvc.services.BeerService;
import com.study.spring6restmvc.services.BeerServiceImpl;
import com.study.spring6restmvcapi.model.BeerDTO;
import com.study.spring6restmvcapi.model.BeerStyle;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static com.study.spring6restmvc.controllers.BeerController.BEER_ID_PATH;
import static com.study.spring6restmvc.controllers.BeerController.BEER_PATH;
import static com.study.spring6restmvc.util.TestUtils.AUTH_HEADER_KEY;
import static com.study.spring6restmvc.util.TestUtils.AUTH_HEADER_MOCK_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BeerController.class)
@Import(JwtDecoderMockConfig.class)
class BeerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BeerService beerService;

    @Captor
    private ArgumentCaptor<UUID> uuidArgumentCaptor;
    @Captor
    private ArgumentCaptor<BeerDTO> beerArgumentCaptor;

    private BeerServiceImpl beerServiceImpl;
    private BeerDTO testBeerDto;


    @BeforeEach
    void setUp() {
        beerServiceImpl = new BeerServiceImpl();
        testBeerDto = beerServiceImpl.getAllBeers(null, null, false, 1, 25).getContent().getFirst();
    }

    @Test
    void patchBeerById() throws Exception {
        var jsonMap = new HashMap<String, String>();
        jsonMap.put("beerName", "Beer Name");

        given(beerService.patchBeerById(any(UUID.class), any(BeerDTO.class))).willReturn(Optional.of(testBeerDto));

        mockMvc.perform(
                        patch(BEER_ID_PATH, testBeerDto.getId())
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(jsonMap)))
                .andExpect(
                        status().isNoContent());

        verify(beerService).patchBeerById(uuidArgumentCaptor.capture(), beerArgumentCaptor.capture());

        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(testBeerDto.getId());
        assertThat(beerArgumentCaptor.getValue().getBeerName()).isEqualTo(jsonMap.get("beerName"));
    }

    @Test
    void deleteBeer() throws Exception {
        given(beerService.deleteBeerById(any(UUID.class))).willReturn(true);

        mockMvc.perform(
                        delete(BEER_ID_PATH, testBeerDto.getId())
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(
                        status().isNoContent());

        verify(beerService).deleteBeerById(uuidArgumentCaptor.capture());

        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(testBeerDto.getId());
    }

    @Test
    void updateBeerById() throws Exception {
        given(beerService.updateBeerById(any(UUID.class), any(BeerDTO.class))).willReturn(Optional.of(testBeerDto));

        mockMvc.perform(
                        put(BEER_ID_PATH, testBeerDto.getId())
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testBeerDto)))
                .andExpect(
                        status().isNoContent());

        verify(beerService).updateBeerById(uuidArgumentCaptor.capture(), eq(testBeerDto));

        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(testBeerDto.getId());
    }

    @Test
    void updateBeerByIdWithBlankNameReturnsBadRequest() throws Exception {
        testBeerDto.setBeerName("");
        given(beerService.updateBeerById(any(UUID.class), any(BeerDTO.class))).willReturn(Optional.of(testBeerDto));

        mockMvc.perform(put(BEER_ID_PATH, testBeerDto.getId())
                        .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBeerDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.length()", is(1)));
    }

    @Test
    void createBeer() throws Exception {
        var beerDto = BeerDTO.builder()
                .beerName("Beer Name")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("123")
                .price(new BigDecimal("11.11"))
                .build();

        given(beerService.saveBeer(any(BeerDTO.class))).willReturn(testBeerDto);

        mockMvc.perform(
                        post(BEER_PATH)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerDto)))
                .andExpectAll(
                        status().isCreated(),
                        header().exists("Location"),
                        redirectedUrlTemplate(BEER_ID_PATH, testBeerDto.getId()));
    }

    @Test
    void createBeerWithNullNameReturnsBadRequest() throws Exception {
        testBeerDto = BeerDTO.builder().build();
        given(beerService.saveBeer(any(BeerDTO.class))).willReturn(testBeerDto);

        mockMvc.perform(
                        post(BEER_PATH)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testBeerDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.length()", is(4)));
    }

    @Test
    void getAllBeers() throws Exception {
        given(beerService.getAllBeers(any(), any(), any(), any(), any())).willReturn(beerServiceImpl.getAllBeers(null, null, false, 1, 25));

        mockMvc.perform(
                        get(BEER_PATH)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content.length()", is(3)));

        verify(beerService).getAllBeers(any(), any(), any(), any(), any());
    }

    @Test
    void getBeerById_NotFound() throws Exception {
        var optionalBeerDto = beerServiceImpl.getBeerById(UUID.randomUUID());

        given(beerService.getBeerById(any(UUID.class))).willReturn(optionalBeerDto);

        mockMvc.perform(
                        get(BEER_ID_PATH, UUID.randomUUID())
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE))
                .andExpect(
                        status().isNotFound());
    }

    @Test
    void getBeerById() throws Exception {
        var beerDtoId = testBeerDto.getId();

        given(beerService.getBeerById(beerDtoId)).willReturn(Optional.of(testBeerDto));

        mockMvc.perform(
                        get(BEER_ID_PATH, beerDtoId)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id", is(testBeerDto.getId().toString())),
                        jsonPath("$.beerName", is(testBeerDto.getBeerName())));
    }
}
