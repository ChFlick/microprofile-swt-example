package de.fhdo.swt.example.controller;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.SimplyTimed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class GreetingController {
    @ConfigProperty(name = "welcome.message") String message;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String greeting() {
        return message;
    }

    @GET
    @Path("/metricsTest")
    @Counted(name = "metricsTestCount", description = "How often the method was called.")
    @SimplyTimed(name = "metricsTestTime", description = "A measure of how long it takes to call the method.")
    @Metered(name = "metricsTestRate", description = "The rate at which the method was called")
    public String metricsTest() {
        return "Check http://localhost:8080/metrics to see the metrics";
    }
}
