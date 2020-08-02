package de.fhdo.swt.example.controller;

import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class FilesControllerTest {

    @BeforeEach
    public void removeFiles() throws IOException {
        cleanUp();
    }

    @AfterAll
    static void cleanUp() throws IOException {
        final File storageDirectory = new File("./storage/");
        FileUtils.deleteDirectory(storageDirectory);
        storageDirectory.mkdir();
    }

    @Test
    void filesPage_canBeRendered() {
        given()
        .when()
            .get("/files")
        .then()
            .body(containsString("No Files uploaded yet."))
            .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void upload_uploadsFiles() {
        given()
            .multiPart("file", new File("./src/test/resources/testfile.txt"), "text/plain")
        .when()
            .post("/files")
        .then()
            .body(containsString("Upload successful"))
            .body(containsString("testfile.txt"))
            .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void download_downloads_uploadedFiles() throws IOException {
        final File testFile = new File("./src/test/resources/testfile.txt");
        final File storageDirectory = new File("./storage/");
        FileUtils.copyFileToDirectory(testFile, storageDirectory);

        InputStream downloadedFile = given()
        .when()
            .get("/files/testfile.txt")
        .then()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=testfile.txt")
            .statusCode(HttpStatus.SC_OK)
            .extract().asInputStream();

        assertTrue(IOUtils.contentEquals(downloadedFile, FileUtils.openInputStream(testFile)));
    }
}