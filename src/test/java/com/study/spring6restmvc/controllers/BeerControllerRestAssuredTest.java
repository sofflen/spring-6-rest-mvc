package com.study.spring6restmvc.controllers;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.atlassian.oai.validator.whitelist.ValidationErrorsWhitelist;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.atlassian.oai.validator.whitelist.rule.WhitelistRules.messageHasKey;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesRegex;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(BeerControllerRestAssuredTest.TestConfig.class)
@Transactional
class BeerControllerRestAssuredTest {

    @LocalServerPort
    Integer localPort;
    @Autowired
    ObjectMapper objectMapper;

    OpenApiValidationFilter apiValidationFilter = new OpenApiValidationFilter(OpenApiInteractionValidator
            .createForSpecificationUrl("oa3.yaml")
            .withWhitelist(ValidationErrorsWhitelist.create()
                    //For educational purposes, OpenAPI Specification should be updated otherwise
                    .withRule("Ignore date format", messageHasKey("validation.response.body.schema.format.date-time")))
            .build());

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = localPort;
    }

    @Test
    void testGetAllBeers() {
        given().contentType(ContentType.JSON)
                .when()
                .filter(apiValidationFilter)
                .get("/api/v1/beer")
                .then()
                .statusCode(200);
    }

    @Test
    void testGetAllBeersByBeerName() {
        given().queryParam("beerName", "Galaxy Cat")
                .when()
                .filter(apiValidationFilter)
                .get("/api/v1/beer")
                .then()
                .statusCode(200)
                .body("content[0].beerName", equalTo("Galaxy Cat"));
    }

    @Test
    void testGetAllBeersWithPageParams() {
        given()
                .queryParams("pageNumber", "1", "pageSize", "2")
                .when()
                .filter(apiValidationFilter)
                .get("/api/v1/beer")
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(2))
                .body("page.number", equalTo(0));
    }

    @Test
    void testGetBeerById() {
        given().pathParam("id", UUID.fromString("93026e42-6cf7-470d-8ce4-711e24a470ef"))
                .when()
                .filter(apiValidationFilter)
                .get("/api/v1/beer/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo("93026e42-6cf7-470d-8ce4-711e24a470ef"))
                .body("beerName", equalTo("Galaxy Cat"));
    }

    @Test
    void testCreateBeer() {
        String testBeerDto = "{\"beerName\":\"Test Beer\",\"upc\":\"123123\",\"beerStyle\":\"IPA\",\"quantityOnHand\":100,\"price\":10}";

        given()
                .header("Content-Type", "application/json")
                .body(testBeerDto)
                .when()
                .filter(apiValidationFilter)
                .post("/api/v1/beer")
                .then()
                .statusCode(201)
                .header("Location", matchesRegex("/api/v1/beer/.+"));

    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(customizer -> customizer.anyRequest().permitAll())
                    .csrf(AbstractHttpConfigurer::disable);

            return http.build();
        }
    }
}
