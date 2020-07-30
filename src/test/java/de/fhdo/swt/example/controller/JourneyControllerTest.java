package de.fhdo.swt.example.controller;

import de.fhdo.swt.example.entity.Journey;
import de.fhdo.swt.example.repository.JourneyRepository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
class JourneyControllerTest {
    @Inject
    JourneyRepository journeyRepository;

    @AfterEach
    @Transactional
    void tearDown() {
        journeyRepository.deleteAll();
    }

    @Transactional
    long addJourney() {
        Journey journey = new Journey();
        journey.setOrigin("here");
        journey.setDestination("there");
        journeyRepository.persist(journey);

        return journey.getId();
    }

    @Test
    void journeyForm_canBeAccessed_noJourneysDisplayed() {
        given()
        .when()
            .get("/journey")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body(containsString("<h2>No journeys yet!</h2>"));
    }

    @Test
    void addJourneyForm_canBeRendered() {
        given()
        .when()
            .get("/journey/add")
        .then()
            .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void addJourney_successful_showsOverview() {
        given()
            .contentType(ContentType.URLENC)
            .param("origin", "here")
            .param("destination", "there")
        .when()
            .post("/journey/add")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body(containsString("<td>here</td>"))
            .body(containsString("<td>there</td>"))
            .body(containsString("<td><a href=\"/journey/edit/"))
            .body(containsString("<td><a href=\"/journey/delete/"));
    }

    @Test
    void addJourney_withErrors_showsErrors() {
        given()
            .contentType(ContentType.URLENC)
            .param("origin", "")
            .param("destination", "")
        .when()
            .post("/journey/add")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body(containsString("<span>Origin is mandatory</span>"))
            .body(containsString("<span>Destination is mandatory</span>"));
    }

    @Test
    void updateForm_canBeRendered() {
        long id = addJourney();

        given()
        .when()
            .get("/journey/edit/" + id)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body(containsString("<input id=\"origin\" placeholder=\"Origin\" name=\"origin\" type=\"text\" value=\"here\">"))
            .body(containsString("<input id=\"destination\" placeholder=\"Destination\" name=\"destination\" type=\"text\" value=\"there\">"));
    }

    @Test
    void editJourney_successful_showsOverview() {
        long id = addJourney();

        given()
            .contentType(ContentType.URLENC)
            .param("origin", "newhere")
            .param("destination", "newthere")
        .when()
            .post("/journey/update/" + id)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body(containsString("<td>newhere</td>"))
            .body(containsString("<td>newthere</td>"))
            .body(containsString("<td><a href=\"/journey/edit/" + id + "\">Edit</a></td>"))
            .body(containsString("<td><a href=\"/journey/delete/" + id + "\">Delete</a></td>"));
    }

    @Test
    void editJourney_withErrors_showsErrors() {
        long id = addJourney();

        given()
            .contentType(ContentType.URLENC)
            .param("origin", "")
            .param("destination", "")
        .when()
            .post("/journey/update/" + id)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body(containsString("<span>Origin is mandatory</span>"))
            .body(containsString("<span>Destination is mandatory</span>"));
    }

    @Test
    void deleteJourney_deletesJourney() {
        long id = addJourney();

        given()
        .when()
            .get("/journey/delete/" + id)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body(containsString("<h2>No journeys yet!</h2>"));
    }
}