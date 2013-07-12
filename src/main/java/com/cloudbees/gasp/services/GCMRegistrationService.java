
package com.cloudbees.gasp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/gcm")
public class GCMRegistrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GCMRegistrationService.class.getName());

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response doRegister(@FormParam("regId") String regId) {

        Datastore.register(regId);
        LOGGER.info("Registered: " + regId);

        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("unregister")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response doUnregister(@FormParam("regId") String regId) {

        Datastore.unregister(regId);
        LOGGER.info("Unregistered: " + regId);

        return Response.status(Response.Status.OK).build();
    }
}
