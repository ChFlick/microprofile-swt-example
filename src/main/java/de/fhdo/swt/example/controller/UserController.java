package de.fhdo.swt.example.controller;

import de.fhdo.swt.example.model.User;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import org.jboss.resteasy.annotations.Form;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/user")
@Produces(MediaType.TEXT_HTML)
public class UserController {

    @GET
    public String userForm() {
        return "<!DOCTYPE HTML>\n" +
            "\n" +
            "<html>\n" +
            "<head>\n" +
            "    <title>User Registration</title>\n" +
            "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
            "</head>\n" +
            "<p>\n" +
            "<h1>Form</h1>\n" +
            "<form action=\"/user\" method=\"post\">\n" +
            "    <label for=\"name\">Name:</label>\n" +
            "    <input type=\"text\" id=\"name\" name=\"name\"/>\n" +
            "    <label for=\"email\">Email:</label>\n" +
            "    <input type=\"text\" id=\"email\" name=\"email\"/>\n" +
            "    <p><input type=\"submit\" value=\"Submit\"/> <input type=\"reset\" value=\"Reset\"/></p>\n" +
            "</form>\n" +
            "</body>\n" +
            "</html>";
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String createUser(@Form User user) {
        return "<html>\n" +
            "<head>\n" +
            "    <title>User Registration</title>\n" +
            "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
            "</head>\n" +
            "<p>\n" +
            "<h1>Form</h1>\n" +
            "<form action=\"/user\" method=\"post\">\n" +
            "    <label for=\"name\">Name:</label>\n" +
            "    <input type=\"text\" id=\"name\" name=\"name\"/>\n" +
            "    <label for=\"email\">Email:</label>\n" +
            "    <input type=\"text\" id=\"email\" name=\"email\"/>\n" +
            "    <p><input type=\"submit\" value=\"Submit\"/> <input type=\"reset\" value=\"Reset\"/></p>\n" +
            "</form>\n" +
            "\n" +
            "<h1>Users:</h1>\n" +
            "<p>name: " + user.getName() + "</p>\n" +
            "<p>email: " + user.getEmail() + "</p>\n" +
            "\n" +
            "</body>\n" +
            "</html>";
    }
}
