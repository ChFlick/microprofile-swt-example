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

    @Inject
    Template user;

    @GET
    public TemplateInstance userForm() {
        return user.data("user", new User());
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public TemplateInstance createUser(@Form User user) {
        return this.user.data("user", user);
    }
}
