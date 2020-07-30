package de.fhdo.swt.example.controller;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class GreetingControllerTest {

    @Test
    void greeting_returnsGreetingMessage() {
        given()
            .when()
        .get("/")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body(is("Greetings to all SWT students!"));
    }
}