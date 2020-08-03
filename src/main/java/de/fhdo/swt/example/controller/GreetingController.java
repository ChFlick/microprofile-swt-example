package de.fhdo.swt.example.controller;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.SimplyTimed;
import org.jboss.resteasy.spi.UnhandledException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.temporal.ChronoUnit;
import java.util.Random;

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

    @GET
    @Path("/retries")
    @Retry(maxRetries = 10)
    public String retries() {
        if (Math.random() > 0.9) {
            return "ok";
        }
        throw new RuntimeException();
    }

    @GET
    @Path("/timeout")
    @Timeout(value = 250)
    public String timeout() throws InterruptedException {
        long started = System.currentTimeMillis();

        Thread.sleep(new Random().nextInt(500));
        return "Executed successful after " + (System.currentTimeMillis() - started) + " ms";
    }

    @GET
    @Path("/timeoutFallback")
    @Timeout(value = 250)
    @Fallback(fallbackMethod = "fallback")
    public String timeoutFallback() throws InterruptedException {
        long started = System.currentTimeMillis();

        Thread.sleep(new Random().nextInt(500));
        return "Executed successful after " + (System.currentTimeMillis() - started) + " ms";
    }

    public String fallback() {
        return "This is the fallback!";
    }
}
