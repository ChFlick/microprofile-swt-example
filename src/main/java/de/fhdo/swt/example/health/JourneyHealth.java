package de.fhdo.swt.example.health;

import de.fhdo.swt.example.repository.JourneyRepository;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Liveness
@ApplicationScoped
public class JourneyHealth implements HealthCheck {
    @Inject JourneyRepository journeyRepository;

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse
            .named("Journey Repository")
            .state(journeyRepository.listAll() != null)
            .build();
    }
}
