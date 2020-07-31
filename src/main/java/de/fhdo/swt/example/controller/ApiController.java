package de.fhdo.swt.example.controller;

import de.fhdo.swt.example.entity.Journey;
import de.fhdo.swt.example.exception.JourneyNotFoundException;
import de.fhdo.swt.example.repository.JourneyRepository;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.resteasy.plugins.server.servlet.HttpServletResponseWrapper;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class ApiController {
    private final JourneyRepository journeyRepository;

    public ApiController(JourneyRepository journeyRepository) {
        this.journeyRepository = journeyRepository;
    }

    @GET
    @Path("/journey/{id}")
    public Journey getJourneyById(@PathParam("id") Long id) {
        return journeyRepository.findByIdOptional(id).orElseThrow(JourneyNotFoundException::new);
    }

    @GET
    @Path("/journeys")
    public List<Journey> getAllJourneys() {
        return journeyRepository.listAll();
    }

    @POST
    @Path("/journey")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createJourney(@RequestBody Journey journey) {
        journeyRepository.persist(journey);

        if(!journeyRepository.isPersistent(journey)){
            return Response.notModified().build();
        }

        return Response.ok(journey).status(Status.CREATED).build();
    }

    @PUT
    @Path("/journey/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Journey updateJourney(@RequestBody Journey journey, @PathParam("id") Long id) {
        Journey updateJourney = journeyRepository.findByIdOptional(id)
                                                 .orElseThrow(JourneyNotFoundException::new);
        updateJourney.setOrigin(journey.getOrigin());
        updateJourney.setDestination(journey.getDestination());
        return updateJourney;
    }

    @DELETE
    @Path("/journey/{id}")
    @Transactional
    public void deleteJourney(@PathParam("id") Long id) {
        journeyRepository.deleteById(id);
    }
}
