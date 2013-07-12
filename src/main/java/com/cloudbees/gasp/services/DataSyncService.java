
package com.cloudbees.gasp.services;

import com.cloudbees.gasp.model.Review;
import com.google.android.gcm.server.Message;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/gasp")
public class DataSyncService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSyncService.class.getName());

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void reviewUpdateReceived(String jsonInput) {
        Review review = new Gson().fromJson(jsonInput, Review.class);
        LOGGER.info("New Review");
        LOGGER.info("  Id: " + String.valueOf(review.getId()));
        LOGGER.info("  Comment: " + review.getComment());
        LOGGER.info("  Star: " + review.getStar());
        LOGGER.info("  Restaurant_id: " + String.valueOf(review.getRestaurant_id()));
        LOGGER.info("  User_id:" + String.valueOf(review.getUser_id()));

        try {
            GCMMessageService messageService = new GCMMessageService();
            Message message = new Message.Builder().build();
            messageService.sendMessage(message);
        } catch (IOException e) {
            LOGGER.error("Error sending GCM message", e);
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String infoPage() {
        return "CloudBees Gasp! GCM Server";
    }


}
