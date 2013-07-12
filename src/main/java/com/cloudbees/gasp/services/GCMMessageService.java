package com.cloudbees.gasp.services;

import com.google.android.gcm.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GCMMessageService {
    private static final int MULTICAST_SIZE = 1000;
    private static final String key = "AIzaSyD8RPFcX_YY3-M21yGGaww2_NBPLHsjU5o";
    private Sender sender = new Sender(key);

    private static final Logger LOGGER = LoggerFactory.getLogger(GCMMessageService.class.getName());
    private static final Executor threadPool = Executors.newFixedThreadPool(5);

    public void sendMessage() throws IOException {
        List<String> devices = Datastore.getDevices();

        if (devices.isEmpty()) {
            LOGGER.error("Message ignored as there is no device registered!");
        } else {
            if (devices.size() == 1) {
                String registrationId = devices.get(0);
                Message message = new Message.Builder().build();
                Result result = sender.send(message, registrationId, 5);
                LOGGER.info("Sent message to one device: " + result);
            } else {
                int total = devices.size();
                List<String> partialDevices = new ArrayList<String>(total);
                int counter = 0;
                int tasks = 0;
                for (String device : devices) {
                    counter++;
                    partialDevices.add(device);
                    int partialSize = partialDevices.size();
                    if (partialSize == MULTICAST_SIZE || counter == total) {
                        asyncSend(partialDevices);
                        partialDevices.clear();
                        tasks++;
                    }
                }
                LOGGER.info("Asynchronously sending " + tasks + " multicast messages to " +
                        total + " devices");
            }
        }
    }

    private void asyncSend(List<String> partialDevices) {
        // make a copy
        final List<String> devices = new ArrayList<String>(partialDevices);
        threadPool.execute(new Runnable() {

            public void run() {
                Message message = new Message.Builder().build();
                MulticastResult multicastResult;
                try {
                    multicastResult = sender.send(message, devices, 5);
                } catch (IOException e) {
                    LOGGER.error("Error posting messages", e);
                    return;
                }
                List<Result> results = multicastResult.getResults();
                // analyze the results
                for (int i = 0; i < devices.size(); i++) {
                    String regId = devices.get(i);
                    Result result = results.get(i);
                    String messageId = result.getMessageId();
                    if (messageId != null) {
                        LOGGER.info("Succesfully sent message to device: " + regId +
                                "; messageId = " + messageId);
                        String canonicalRegId = result.getCanonicalRegistrationId();
                        if (canonicalRegId != null) {
                            // same device has more than on registration id: update it
                            LOGGER.info("canonicalRegId " + canonicalRegId);
                            Datastore.updateRegistration(regId, canonicalRegId);
                        }
                    } else {
                        String error = result.getErrorCodeName();
                        if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                            // application has been removed from device - unregister it
                            LOGGER.info("Unregistered device: " + regId);
                            Datastore.unregister(regId);
                        } else {
                            LOGGER.error("Error sending message to " + regId + ": " + error);
                        }
                    }
                }
            }
        });
    }
}
