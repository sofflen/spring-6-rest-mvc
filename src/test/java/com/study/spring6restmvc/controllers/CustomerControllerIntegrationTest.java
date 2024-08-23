package com.study.spring6restmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.spring6restmvc.entities.Customer;
import com.study.spring6restmvc.exceptions.NotFoundException;
import com.study.spring6restmvc.mappers.CustomerMapper;
import com.study.spring6restmvc.model.CustomerDTO;
import com.study.spring6restmvc.repositories.CustomerRepository;
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

import static com.study.spring6restmvc.controllers.CustomerController.CUSTOMER_PATH;
import static com.study.spring6restmvc.controllers.CustomerController.CUSTOMER_PATH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CustomerControllerIntegrationTest {

    @Autowired
    private CustomerController customerController;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext wac;

    private Customer testCustomer;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.findAll().getFirst();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void testGetAllCustomers() {
        var customerDtoPage = customerController.getAllCustomers(null, null, 1, 2000);

        assertThat(customerDtoPage).isNotNull();
        assertThat(customerDtoPage.getContent().size()).isEqualTo(1000);
    }

    @Test
    @Transactional
    void testGetAllCustomersReturnsEmptyListIfNoCustomers() {
        customerRepository.deleteAll();

        var customerDtoPage = customerController.getAllCustomers(null, null, 1, 25);

        assertThat(customerDtoPage).isNotNull();
        assertThat(customerDtoPage.isEmpty()).isTrue();
    }

    @Test
    void testGetAllCustomersWithQueryParamCustomerName() throws Exception {
        mockMvc.perform(get(CUSTOMER_PATH)
                        .queryParam("customerName", "john"))
                .andExpectAll(status().isOk(),
                        jsonPath("$.size()", greaterThan(10)));
    }

    @Test
    void testGetAllCustomersWithQueryParamEmail() throws Exception {
        mockMvc.perform(get(CUSTOMER_PATH)
                        .queryParam("email", "john.doe@gmail.com"))
                .andExpectAll(status().isOk(),
                        jsonPath("$.content.size()", is(1)));
    }

    @Test
    void testGetAllCustomersWithQueryParamCustomerNameAndEmail() throws Exception {
        mockMvc.perform(get(CUSTOMER_PATH)
                        .queryParam("customerName", "john")
                        .queryParam("email", "john.doe@gmail.com"))
                .andExpectAll(status().isOk(),
                        jsonPath("$.content.size()", is(1)));
    }

    @Test
    void testGetAllCustomersWithQueryParamCustomerNamePageTwo() throws Exception {
        mockMvc.perform(get(CUSTOMER_PATH)
                        .queryParam("customerName", "jo")
                        .queryParam("pageNumber", "2")
                        .queryParam("pageSize", "30"))
                .andExpectAll(status().isOk(),
                        jsonPath("$.content.size()", is(30)));
    }

    @Test
    void testGetAllCustomersWithBadQueryParamsThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> customerController
                .getAllCustomers("john", "john.doe@bad.email", 1, 25));
    }

    @Test
    void getCustomerById() {
        var customer = customerRepository.findAll().getFirst();
        var customerDto = customerController.getCustomerById(customer.getId());

        assertThat(customerDto).isNotNull();
    }

    @Test
    void getCustomerByIdThrowsNotFoundExceptionIfCustomerDoesNotExist() {
        assertThrows(NotFoundException.class,
                () -> customerController.getCustomerById(UUID.randomUUID()));
    }

    @Test
    @Transactional
    void testCreateCustomer() {
        var customerDto = CustomerDTO.builder()
                .customerName("New Customer")
                .build();

        var responseEntity = customerController.createCustomer(customerDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        var locationSplitStrArray = responseEntity.getHeaders().getLocation().toString().split("/");
        var customerId = UUID.fromString(locationSplitStrArray[locationSplitStrArray.length - 1]);

        assertThat(customerRepository.findById(customerId)).isNotNull();
    }

    @Test
    void testUpdateCustomerThrowsNotFoundExceptionIfCustomerDoesNotExist() {
        assertThrows(NotFoundException.class,
                () -> customerController.updateCustomerById(UUID.randomUUID(), CustomerDTO.builder().build()));
    }

    @Test
    @Transactional
    void testUpdateCustomer() {
        var testCustomer = customerRepository.findAll().getFirst();
        var customerDto = customerMapper.customerToCustomerDTO(testCustomer);
        final String newCustomerName = "New Customer";

        customerDto.setId(null);
        customerDto.setVersion(null);
        customerDto.setCustomerName(newCustomerName);

        var responseEntity = customerController.updateCustomerById(testCustomer.getId(), customerDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        var updatedCustomer = customerRepository.findById(testCustomer.getId()).orElseThrow();

        assertThat(updatedCustomer.getCustomerName()).isEqualTo(newCustomerName);
    }

    @Test
    @Transactional
    void testDeleteCustomerById() {
        var customerId = testCustomer.getId();

        var ResponseEntity = customerController.deleteCustomerById(customerId);

        assertThat(ResponseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(customerRepository.findById(customerId).isEmpty()).isTrue();
    }

    @Test
    void testDeleteByIdThrowsNotFoundExceptionIfCustomerDoesNotExist() {
        assertThrows(NotFoundException.class,
                () -> customerController.deleteCustomerById(UUID.randomUUID()));
    }

    @Test
    @Transactional
    void testPatchCustomerById() {
        var testCustomer = customerRepository.findAll().getFirst();
        var customerDto = customerMapper.customerToCustomerDTO(testCustomer);
        final String newCustomerName = "New Customer";

        customerDto.setId(null);
        customerDto.setVersion(null);
        customerDto.setCustomerName(newCustomerName);

        var responseEntity = customerController.patchCustomerById(testCustomer.getId(), customerDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        var updatedCustomer = customerRepository.findById(testCustomer.getId()).orElseThrow();

        assertThat(updatedCustomer.getCustomerName()).isEqualTo(newCustomerName);
    }

    @Test
    void testPatchCustomerByIdWithTooLongNameReturnsBadRequest() throws Exception {
        var jsonMap = new HashMap<String, String>();
        String tooLongName = "Customer Name 012345678901234567890123456789012345678901234567890123456789012345678901234567890";
        jsonMap.put("customerName", tooLongName);


        mockMvc.perform(patch(CUSTOMER_PATH_ID, testCustomer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonMap)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)));
    }
}
