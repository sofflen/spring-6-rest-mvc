package com.study.spring6restmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.spring6restmvc.model.Customer;
import com.study.spring6restmvc.services.CustomerService;
import com.study.spring6restmvc.services.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
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
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    private CustomerServiceImpl customerServiceImpl;

    @BeforeEach
    void setUp() {
        customerServiceImpl = new CustomerServiceImpl();
    }

    @Test
    void patchCustomerById() throws Exception {
        Customer testCustomer = customerServiceImpl.getAllCustomers().getFirst();
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("customerName", "Customer Name");

        mockMvc.perform(patch("/api/v1/customer/{id}", testCustomer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonMap)))
                .andExpect(status().isNoContent());

        verify(customerService).patchCustomerById(uuidArgumentCaptor.capture(), customerArgumentCaptor.capture());

        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(testCustomer.getId());
        assertThat(customerArgumentCaptor.getValue().getCustomerName()).isEqualTo(jsonMap.get("customerName"));
    }

    @Test
    void deleteCustomer() throws Exception {
        Customer testCustomer = customerServiceImpl.getAllCustomers().getFirst();

        mockMvc.perform(delete("/api/v1/customer/{id}", testCustomer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(customerService).deleteCustomerById(uuidArgumentCaptor.capture());

        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(testCustomer.getId());
    }

    @Test
    void updateCustomerById() throws Exception {
        Customer testCustomer = customerServiceImpl.getAllCustomers().getFirst();

        mockMvc.perform(put("/api/v1/customer/{id}", testCustomer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomer)))
                .andExpect(status().isNoContent());

        verify(customerService).updateCustomerById(testCustomer.getId(), testCustomer);

    }

    @Test
    void createCustomer() throws Exception {
        Customer testCustomer = Customer.builder().build();

        given(customerService.saveCustomer(any(Customer.class)))
                .willReturn(customerServiceImpl.getAllCustomers().getFirst());

        mockMvc.perform(post("/api/v1/customer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomer)))
                .andExpectAll(status().isCreated(),
                        header().exists("Location"),
                        redirectedUrl("/api/v1/customer/"
                                + customerServiceImpl.getAllCustomers().getFirst().getId()));
    }

    @Test
    void getAllCustomers() throws Exception {
        given(customerService.getAllCustomers()).willReturn(customerServiceImpl.getAllCustomers());

        mockMvc.perform(get("/api/v1/customer"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)));

        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void getCustomerById() throws Exception {
        Customer testCustomer = customerServiceImpl.getAllCustomers().getFirst();

        given(customerService.getCustomerById(testCustomer.getId())).willReturn(testCustomer);

        mockMvc.perform(get("/api/v1/customer/" + testCustomer.getId()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testCustomer.getId().toString())))
                .andExpect(jsonPath("$.customerName", is(testCustomer.getCustomerName())));
    }
}