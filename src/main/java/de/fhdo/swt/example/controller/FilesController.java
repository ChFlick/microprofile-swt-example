package de.fhdo.swt.example.controller;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystemException;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("/files")
public class FilesController {
    private final File storageDirectory = new File("./storage/");
    private final Template uploadTemplate;

    public FilesController(@ResourcePath("files.html") final Template uploadTemplate) {
        this.uploadTemplate = uploadTemplate;
        this.storageDirectory.mkdir();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance filesPage() {
        List<String> fileNames = getAvailableFilenames();
        return uploadTemplate.data("files", fileNames);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance upload(MultipartFormDataInput multipartFormDataInput) {
        InputPart filePart = multipartFormDataInput.getFormDataMap().get("file").get(0);

        try {
            String filename = getFileName(filePart.getHeaders());
            InputStream inputStream = filePart.getBody(InputStream.class, null);
            File file = new File(storageDirectory + File.separator + filename);
            FileUtils.copyInputStreamToFile(inputStream, file);

            List<String> availableFiles = getAvailableFilenames();
            return uploadTemplate.data("files", availableFiles).data("message", "Upload successful.");
        } catch (IOException e) {
            e.printStackTrace();

            List<String> availableFiles = getAvailableFilenames();
            return uploadTemplate.data("files", availableFiles)
                                 .data("message", "File could not be uploaded - " + e.getMessage());
        }
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(@PathParam("name") String filename) {
        File fileDownload = new File(storageDirectory + File.separator + filename);
        return Response.ok(fileDownload)
                       .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename)
                       .build();
    }

    private String getFileName(MultivaluedMap<String, String> header) throws FileSystemException {
        String[] contentDisposition = header.getFirst(HttpHeaders.CONTENT_DISPOSITION).split(";");

        return Arrays.stream(contentDisposition)
                     .filter(name -> name.trim().startsWith("filename"))
                     .findFirst()
                     .map(fileNameElement -> fileNameElement.split("=")[1].trim().replaceAll("\"", ""))
                     .orElseThrow(() -> new FileSystemException("No filename found"));
    }

    private List<String> getAvailableFilenames() {
        return FileUtils.listFiles(storageDirectory, TrueFileFilter.INSTANCE, FalseFileFilter.INSTANCE)
                        .stream()
                        .map(File::getName)
                        .collect(toList());
    }
}
