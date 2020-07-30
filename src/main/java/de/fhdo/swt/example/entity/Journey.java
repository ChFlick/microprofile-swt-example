package de.fhdo.swt.example.entity;

import org.jboss.resteasy.annotations.jaxrs.FormParam;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
public class Journey {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @FormParam
    @NotBlank(message = "Origin is mandatory")
    private String origin;

    @FormParam
    @NotBlank(message = "Destination is mandatory")
    private String destination;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}