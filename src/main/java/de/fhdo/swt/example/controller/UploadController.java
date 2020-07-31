package de.fhdo.swt.example.controller;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jboss.resteasy.annotations.jaxrs.HeaderParam;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystemException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Path("/upload")
public class UploadController {
    private final File storageDirectory = new File("./storage/");
    private final Template uploadTemplate;

    public UploadController(@ResourcePath("upload.html") final Template uploadTemplate) {
        this.uploadTemplate = uploadTemplate;
        this.storageDirectory.mkdir();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance uploadForm() {
        List<String> fileNames = FileUtils.listFiles(storageDirectory, TrueFileFilter.INSTANCE, FalseFileFilter.INSTANCE)
                                          .stream()
                                          .map(File::getName)
                                          .collect(toList());
        return uploadTemplate.data("files", fileNames);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public String sendMultipartData(MultipartFormDataInput multipartFormDataInput) throws IOException {
        InputPart filePart = multipartFormDataInput.getFormDataMap().get("file").get(0);
        String[] contentDispositionParts = filePart.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION).split(";");
        String filename = Arrays.stream(contentDispositionParts)
                                .filter(name -> name.trim().startsWith("filename"))
                                .findFirst()
                                .map(fileNameElement -> fileNameElement.split("=")[1].trim().replaceAll("\"", ""))
                                .orElseThrow(() -> new FileSystemException("No filename found"));
        InputStream inputStream = filePart.getBody(InputStream.class, null);

        try {
            File file = new File(storageDirectory + "/" + filename);
            FileUtils.copyInputStreamToFile(inputStream, file);

            return "ok";
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }
}
