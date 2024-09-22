package com.study.spring6restmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.spring6restmvc.config.JwtDecoderMockConfig;
import com.study.spring6restmvc.model.CustomerDTO;
import com.study.spring6restmvc.services.CustomerService;
import com.study.spring6restmvc.services.CustomerServiceImpl;
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

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static com.study.spring6restmvc.controllers.CustomerController.CUSTOMER_PATH;
import static com.study.spring6restmvc.controllers.CustomerController.CUSTOMER_ID_PATH;
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

@WebMvcTest(CustomerController.class)
@Import(JwtDecoderMockConfig.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @Captor
    private ArgumentCaptor<UUID> uuidArgumentCaptor;
    @Captor
    private ArgumentCaptor<CustomerDTO> customerArgumentCaptor;

    private CustomerServiceImpl customerServiceImpl;
    private CustomerDTO testCustomerDto;

    @BeforeEach
    void setUp() {
        customerServiceImpl = new CustomerServiceImpl();
        testCustomerDto = customerServiceImpl.getAllCustomers(null, null, 1, 25).getContent().getFirst();
    }

    @Test
    void patchCustomerById() throws Exception {
        var jsonMap = new HashMap<String, String>();
        jsonMap.put("customerName", "Customer Name");

        given(customerService.patchCustomerById(any(UUID.class), any(CustomerDTO.class)))
                .willReturn(Optional.of(testCustomerDto));

        mockMvc.perform(patch(CUSTOMER_ID_PATH, testCustomerDto.getId())
                        .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonMap)))
                .andExpect(status().isNoContent());

        verify(customerService).patchCustomerById(uuidArgumentCaptor.capture(), customerArgumentCaptor.capture());

        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(testCustomerDto.getId());
        assertThat(customerArgumentCaptor.getValue().getCustomerName()).isEqualTo(jsonMap.get("customerName"));
    }

    @Test
    void deleteCustomer() throws Exception {
        given(customerService.deleteCustomerById(any(UUID.class))).willReturn(true);

        mockMvc.perform(delete(CUSTOMER_ID_PATH, testCustomerDto.getId())
                        .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(customerService).deleteCustomerById(uuidArgumentCaptor.capture());

        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(testCustomerDto.getId());
    }

    @Test
    void updateCustomerById() throws Exception {
        given(customerService.updateCustomerById(any(UUID.class), any(CustomerDTO.class)))
                .willReturn(Optional.of(testCustomerDto));

        mockMvc.perform(put(CUSTOMER_ID_PATH, testCustomerDto.getId())
                        .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomerDto)))
                .andExpect(status().isNoContent());

        verify(customerService).updateCustomerById(uuidArgumentCaptor.capture(), eq(testCustomerDto));

        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(testCustomerDto.getId());
    }

    @Test
    void updateCustomerByIdWithBlankNameReturnsBadRequest() throws Exception {
        testCustomerDto.setCustomerName("");
        given(customerService.updateCustomerById(any(UUID.class), any(CustomerDTO.class))).willReturn(Optional.of(testCustomerDto));

        mockMvc.perform(put(CUSTOMER_ID_PATH, testCustomerDto.getId())
                        .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void createCustomer() throws Exception {
        var customerDTO = CustomerDTO.builder()
                .customerName("Customer Name")
                .build();

        given(customerService.saveCustomer(any(CustomerDTO.class))).willReturn(testCustomerDto);

        mockMvc.perform(post(CUSTOMER_PATH)
                        .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpectAll(status().isCreated(),
                        header().exists("Location"),
                        redirectedUrlTemplate(CUSTOMER_ID_PATH, testCustomerDto.getId()));
    }

    @Test
    void createCustomerWithNullNameReturnsBadRequest() throws Exception {
        testCustomerDto = CustomerDTO.builder().build();
        given(customerService.saveCustomer(any(CustomerDTO.class))).willReturn(testCustomerDto);

        mockMvc.perform(post(CUSTOMER_PATH)
                        .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andReturn();
    }

    @Test
    void getAllCustomers() throws Exception {
        given(customerService.getAllCustomers(any(), any(), any(), any())).willReturn(customerServiceImpl.getAllCustomers(null, null, 1, 25));

        mockMvc.perform(get(CUSTOMER_PATH)
                        .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()", is(3)));

        verify(customerService).getAllCustomers(any(), any(), any(), any());
    }

    @Test
    void getCustomerById_NotFound() throws Exception {
        var optionalCustomerDto = customerServiceImpl.getCustomerById(UUID.randomUUID());

        given(customerService.getCustomerById(any(UUID.class))).willReturn(optionalCustomerDto);

        mockMvc.perform(get(CUSTOMER_ID_PATH, UUID.randomUUID())
                        .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCustomerById() throws Exception {
        var customerDtoId = testCustomerDto.getId();

        given(customerService.getCustomerById(customerDtoId)).willReturn(Optional.of(testCustomerDto));

        mockMvc.perform(get(CUSTOMER_ID_PATH, testCustomerDto.getId())
                        .header(AUTH_HEADER_KEY, AUTH_HEADER_MOCK_VALUE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(customerDtoId.toString())))
                .andExpect(jsonPath("$.customerName", is(testCustomerDto.getCustomerName())));
    }
}
