package de.fhdo.swt.example.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
class UserControllerTest {

    @Test
    void userForm_canBeRendered() {
        given()
        .when()
            .get("/user")
        .then()
            .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void createUser_displaysCreatedUser() {
        given()
            .param("name", "testname")
            .param("email", "testemail")
            .contentType(ContentType.URLENC)
        .when()
            .post("/user")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body(containsString("<p>name: testname</p>"))
            .body(containsString("<p>email: testemail</p>"));

    }
}