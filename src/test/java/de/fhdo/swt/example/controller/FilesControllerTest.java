package de.fhdo.swt.example.controller;

import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
    @ConfigProperty(name = "files.storage.dir")
    String filesDir;

    private File storageDirectory;
    private static final File TEST_FILE = new File("./src/test/resources/testfile.txt");

    @BeforeEach
    @AfterEach
    public void removeFiles() throws IOException {
        this.storageDirectory = new File(filesDir);
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
            .multiPart("file", TEST_FILE, "text/plain")
        .when()
            .post("/files")
        .then()
            .body(containsString("Upload successful"))
            .body(containsString("testfile.txt"))
            .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void download_downloads_uploadedFiles() throws IOException {
        FileUtils.copyFileToDirectory(TEST_FILE, storageDirectory);

        InputStream downloadedFile = given()
        .when()
            .get("/files/testfile.txt")
        .then()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=testfile.txt")
            .statusCode(HttpStatus.SC_OK)
            .extract().asInputStream();

        assertTrue(IOUtils.contentEquals(downloadedFile, FileUtils.openInputStream(TEST_FILE)));
    }
}