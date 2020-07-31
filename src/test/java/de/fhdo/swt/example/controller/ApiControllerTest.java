package de.fhdo.swt.example.controller;

import de.fhdo.swt.example.entity.Journey;
import de.fhdo.swt.example.repository.JourneyRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ApiControllerTest {
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
    void getJourneyById_journeyNotAvailable_returnsError() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/journey/1")
        .then()
            .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void getJourneyById_returnsJourney() {
        long journeyId = addJourney();

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/journey/" + journeyId)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("origin", equalTo("here"))
            .body("destination", equalTo("there"))
            .body("id", equalTo((int) journeyId));
    }

    @Test
    void getAllJourneys_withoutJourneys_returnsEmptyList() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/journeys")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("", hasSize(0));
    }

    @Test
    void getAllJourneys_withJourneys_returnsList() {
        long journeyId1 = addJourney();
        long journeyId2 = addJourney();

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/journeys")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("", hasSize(2))
            .body("id", hasItems((int) journeyId1, (int) journeyId2))
            .body("destination", hasItems("there", "there"))
            .body("origin", hasItems("here", "here"));
    }

    @Test
    void createJourney_Returns201Created() {
        Journey journey = new Journey();
        journey.setOrigin("here");
        journey.setDestination("there");

        given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(journey)
        .when()
            .post("/api/journey")
        .then()
            .statusCode(HttpStatus.SC_CREATED)
            .body("id", greaterThan(0))
            .body("destination", equalTo("there"))
            .body("origin", equalTo("here"));
    }

    @Test
    void updateJourney_journeyNotAvailable_returnsError() {
        Journey journey = new Journey();
        journey.setOrigin("here");
        journey.setDestination("there");
        journey.setId(1);

        given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(journey)
        .when()
            .put("/api/journey/1")
        .then()
            .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void updateJourney_updatesJourney() {
        long journeyId = addJourney();

        Journey journey = new Journey();
        journey.setOrigin("newHere");
        journey.setDestination("newThere");
        journey.setId(journeyId);

        given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(journey)
        .when()
            .put("/api/journey/" + journeyId)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("origin", equalTo("newHere"))
            .body("destination", equalTo("newThere"))
            .body("id", equalTo((int) journeyId));

        assertThat(journeyRepository.findById(journeyId).getOrigin(), is(equalTo("newHere")));
        assertThat(journeyRepository.findById(journeyId).getDestination(), is(equalTo("newThere")));
    }

    @Test
    void deleteJourney_deletesJourney() {
        long journeyId = addJourney();

        given()
        .when()
            .delete("/api/journey/" + journeyId)
        .then()
            .statusCode(HttpStatus.SC_NO_CONTENT);

        assertThat(journeyRepository.findByIdOptional(journeyId).isPresent(), is(false));
    }
}