package de.fhdo.swt.example.controller;

import de.fhdo.swt.example.entity.Journey;
import de.fhdo.swt.example.exception.JourneyNotFoundException;
import de.fhdo.swt.example.repository.JourneyRepository;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
    @APIResponses(
        value = {
            @APIResponse(
                responseCode = "200",
                description = "Successfully retrieved the journey",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Journey.class)
                )
            ),
            @APIResponse(
                responseCode = "404",
                description = "The journey was not found",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)
            )
        }
    )
    @Operation(
        summary = "Fetch a specific journey",
        description = "Retrieves a specific journey by its id from the database."
    )
    public Journey getJourneyById(
        @Parameter(
            description = "The id of the journey to look up.",
            required = true,
            example = "1",
            schema = @Schema(type = SchemaType.INTEGER)
        )
        @PathParam("id") Long id) {
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
