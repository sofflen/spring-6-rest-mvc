package com.study.spring6restmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.spring6restmvc.config.JwtDecoderConfig;
import com.study.spring6restmvc.entities.Customer;
import com.study.spring6restmvc.exceptions.NotFoundException;
import com.study.spring6restmvc.mappers.CustomerMapper;
import com.study.spring6restmvc.model.CustomerDTO;
import com.study.spring6restmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
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
import static com.study.spring6restmvc.util.TestUtils.AUTH_HEADER_GENERATED_VALUE;
import static com.study.spring6restmvc.util.TestUtils.AUTH_HEADER_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(JwtDecoderConfig.class)
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
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    void testGetAllCustomers() {
        var customerDTOPagedModel = customerController.getAllCustomers(null, null, 1, 2000);

        assertThat(customerDTOPagedModel).isNotNull();
        assertThat(customerDTOPagedModel.getContent()).hasSize(1000);
    }

    @Test
    @Transactional
    void testGetAllCustomersReturnsEmptyListIfNoCustomers() {
        customerRepository.deleteAll();

        var customerDTOPagedModel = customerController.getAllCustomers(null, null, 1, 25);

        assertThat(customerDTOPagedModel).isNotNull();
        assertThat(customerDTOPagedModel.getContent()).isEmpty();
    }

    @Test
    void testGetAllCustomersWithQueryParamCustomerName() throws Exception {
        mockMvc.perform(
                        get(CUSTOMER_PATH)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE)
                                .queryParam("customerName", "john"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content.size()", greaterThan(10)));
    }

    @Test
    void testGetAllCustomersWithQueryParamEmail() throws Exception {
        mockMvc.perform(
                        get(CUSTOMER_PATH)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE)
                                .queryParam("email", "john.doe@gmail.com"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content.size()", is(1)));
    }

    @Test
    void testGetAllCustomersWithQueryParamCustomerNameAndEmail() throws Exception {
        mockMvc.perform(
                        get(CUSTOMER_PATH)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE)
                                .queryParam("customerName", "john")
                                .queryParam("email", "john.doe@gmail.com"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content.size()", is(1)));
    }

    @Test
    void testGetAllCustomersWithQueryParamCustomerNamePageTwo() throws Exception {
        mockMvc.perform(
                        get(CUSTOMER_PATH)
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE)
                                .queryParam("customerName", "jo")
                                .queryParam("pageNumber", "2")
                                .queryParam("pageSize", "30"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content.size()", is(30)));
    }

    @Test
    void testGetAllCustomersWithBadQueryParamsThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> customerController
                .getAllCustomers("john", "john.doe@bad.email", 1, 25));
    }

    @Test
    void getCustomerById() {
        var customerDto = customerController.getCustomerById(testCustomer.getId());

        assertThat(customerDto).isNotNull();
    }

    @Test
    void getCustomerByIdThrowsNotFoundExceptionIfCustomerDoesNotExist() {
        var randomUuid = UUID.randomUUID();

        assertThrows(NotFoundException.class,
                () -> customerController.getCustomerById(randomUuid));
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
        var randomUuid = UUID.randomUUID();
        var customerDto = CustomerDTO.builder().build();

        assertThrows(NotFoundException.class,
                () -> customerController.updateCustomerById(randomUuid, customerDto));
    }

    @Test
    @Transactional
    void testUpdateCustomer() {
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

        var responseEntity = customerController.deleteCustomerById(customerId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(customerRepository.findById(customerId)).isEmpty();
    }

    @Test
    void testDeleteByIdThrowsNotFoundExceptionIfCustomerDoesNotExist() {
        var randomUuid = UUID.randomUUID();

        assertThrows(NotFoundException.class,
                () -> customerController.deleteCustomerById(randomUuid));
    }

    @Test
    @Transactional
    void testPatchCustomerById() {
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


        mockMvc.perform(
                        patch(CUSTOMER_PATH_ID, testCustomer.getId())
                                .header(AUTH_HEADER_KEY, AUTH_HEADER_GENERATED_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(jsonMap)))
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.length()", is(1)));
    }

    @Test
    void testGetAllCustomersWithNoAuthReturnsUnauthorized() throws Exception {
        mockMvc.perform(
                        get(CUSTOMER_PATH)
                                .queryParam("customerName", "john"))
                .andExpect(
                        status().isUnauthorized());
    }
}
