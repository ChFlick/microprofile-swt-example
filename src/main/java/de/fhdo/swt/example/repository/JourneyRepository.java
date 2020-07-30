package de.fhdo.swt.example.repository;

import de.fhdo.swt.example.entity.Journey;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JourneyRepository implements PanacheRepository<Journey> {
}
