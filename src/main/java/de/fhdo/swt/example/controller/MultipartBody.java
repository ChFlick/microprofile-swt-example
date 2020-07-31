package de.fhdo.swt.example.controller;

import org.jboss.resteasy.annotations.jaxrs.FormParam;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.core.MediaType;
import java.io.InputStream;

public class MultipartBody {
    @FormParam
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream file;
}
