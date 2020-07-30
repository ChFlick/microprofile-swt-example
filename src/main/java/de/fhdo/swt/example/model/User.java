package de.fhdo.swt.example.model;

import org.jboss.resteasy.annotations.jaxrs.FormParam;

public class User {
    private long id;

    @FormParam
    private String name;

    @FormParam
    private String email;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}