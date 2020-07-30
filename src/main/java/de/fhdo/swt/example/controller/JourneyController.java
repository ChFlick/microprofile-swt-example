package de.fhdo.swt.example.controller;

import de.fhdo.swt.example.entity.Journey;
import de.fhdo.swt.example.exception.JourneyNotFoundException;
import de.fhdo.swt.example.repository.JourneyRepository;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import org.jboss.resteasy.annotations.Form;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

@Path("/journey")
@Produces(MediaType.TEXT_HTML)
public class JourneyController {

    private final JourneyRepository journeyRepository;
    private final Template journey;
    private final Template addJourney;
    private final Template updateJourney;
    private final Validator validator;

    @Inject
    public JourneyController(JourneyRepository journeyRepository,
                             Template journey,
                             Template addJourney,
                             Template updateJourney,
                             Validator validator) {
        this.journeyRepository = journeyRepository;
        this.journey = journey;
        this.addJourney = addJourney;
        this.updateJourney = updateJourney;
        this.validator = validator;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance showJourneyForm() {
        return this.journey.data("journeys", journeyRepository.listAll());
    }

    @GET
    @Path("/add")
    public TemplateInstance showAddJourneyForm() {
        return this.addJourney.instance();
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public TemplateInstance addJourney(@Form Journey journey) {
        Set<ConstraintViolation<Journey>> violations = validator.validate(journey);
        if (!violations.isEmpty()) {
            Map<String, String> errors = violations.stream()
                                                   .collect(toMap(v -> v.getPropertyPath().toString(),
                                                       ConstraintViolation::getMessage));
            return addJourney.data("errors", errors);
        }

        journeyRepository.persist(journey);
        return this.journey.data("journeys", journeyRepository.listAll());
    }

    @GET
    @Path("/edit/{id}")
    public TemplateInstance showJourneyUpdateForm(@PathParam("id") long id) {
        Journey journey = journeyRepository.findByIdOptional(id)
                                           .orElseThrow(() -> new JourneyNotFoundException("Invalid journey Id:" + id));

        return updateJourney.data("journey", journey);
    }

    @POST
    @Path("/update/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public TemplateInstance updateJourney(@PathParam("id") long id, @Form Journey journeyUpdates) {
        Set<ConstraintViolation<Journey>> violations = validator.validate(journeyUpdates);
        if (!violations.isEmpty()) {
            Map<String, String> errors = violations.stream()
                                                   .collect(toMap(v -> v.getPropertyPath().toString(), ConstraintViolation::getMessage));
            journeyUpdates.setId(id);
            return updateJourney.data("errors", errors).data("journey", journeyUpdates);
        }

        Journey journeyToUpdate = journeyRepository.findByIdOptional(id)
                                                  .orElseThrow(() -> new JourneyNotFoundException("Invalid journey Id:" + id));

        journeyToUpdate.setDestination(journeyUpdates.getDestination());
        journeyToUpdate.setOrigin(journeyUpdates.getOrigin());
        return journey.data("journeys", journeyRepository.listAll());
    }

    @GET
    @Path("/delete/{id}")
    @Transactional
    public TemplateInstance deleteJourney(@PathParam("id") long id) {
        Journey journey = journeyRepository.findByIdOptional(id)
                                           .orElseThrow(() -> new JourneyNotFoundException("Invalid journey Id:" + id));
        journeyRepository.delete(journey);
        return this.journey.data("journeys", journeyRepository.listAll());
    }
}

