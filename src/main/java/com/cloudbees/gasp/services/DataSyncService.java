
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
        LOGGER.info("Syncing Id: " + String.valueOf(review.getId()));

        try {
            GCMMessageService messageService = new GCMMessageService(new Config().getKey());
            Message message = new Message.Builder()
                                         .delayWhileIdle(true)
                                         .addData("id", String.valueOf(review.getId()))
                                         .build();
            messageService.sendMessage(message);
        } catch (IOException e) {
            LOGGER.error("Error sending GCM message", e);
        }
    }
}
