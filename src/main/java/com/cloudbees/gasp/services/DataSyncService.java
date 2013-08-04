
package com.cloudbees.gasp.services;

import com.cloudbees.gasp.model.Restaurant;
import com.cloudbees.gasp.model.Review;
import com.cloudbees.gasp.model.User;
import com.google.android.gcm.server.Message;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/")
public class DataSyncService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSyncService.class.getName());

    @POST
    @Path("/reviews")
    @Consumes(MediaType.APPLICATION_JSON)
    public void reviewUpdateReceived(String jsonInput) {
        Review review = new Gson().fromJson(jsonInput, Review.class);
        LOGGER.info("Syncing Review Id: " + String.valueOf(review.getId()));

        try {
            GCMMessageService messageService = new GCMMessageService(new Config().getKey());
            Message message = new Message.Builder()
                                         .delayWhileIdle(true)
                                         .addData("table", "reviews")
                                         .addData("id", String.valueOf(review.getId()))
                                         .build();
            messageService.sendMessage(message);
        } catch (IOException e) {
            LOGGER.error("Error sending GCM message", e);
        }
    }

    @POST
    @Path("/restaurants")
    @Consumes(MediaType.APPLICATION_JSON)
    public void restaurantUpdateReceived(String jsonInput) {
        Restaurant restaurant = new Gson().fromJson(jsonInput, Restaurant.class);
        LOGGER.info("Syncing Restaurant Id: " + String.valueOf(restaurant.getId()));

        try {
            GCMMessageService messageService = new GCMMessageService(new Config().getKey());
            Message message = new Message.Builder()
                                         .delayWhileIdle(true)
                                         .addData("table", "restaurants")
                                         .addData("id", String.valueOf(restaurant.getId()))
                                         .build();
            messageService.sendMessage(message);
        } catch (IOException e) {
            LOGGER.error("Error sending GCM message",e);
        }
    }

    @POST
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    public void userUpdateReceived(String jsonInput) {
        User user = new Gson().fromJson(jsonInput, User.class);
        LOGGER.info("Syncing User Id: " + String.valueOf(user.getId()));

        try {
            GCMMessageService messageService = new GCMMessageService(new Config().getKey());
            Message message = new Message.Builder()
                                         .delayWhileIdle(true)
                                         .addData("table", "users")
                                         .addData("id", String.valueOf(user.getId()))
                                         .build();
            messageService.sendMessage(message);
        } catch (IOException e) {
            LOGGER.error("Error sending GCM message",e);
        }
    }
}
