package com.cloudbees.gasp.services;

import com.google.android.gcm.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *  Generic interface to GCM (Google Cloud Messaging) based on Android SDK GCM sample app
 *  Supports asynchronous multicasting of messages to 5 devices at a time
 */
public class GCMMessageService {
    private static final int MULTICAST_SIZE = 1000;
    private static final String key = "AIzaSyD8RPFcX_YY3-M21yGGaww2_NBPLHsjU5o";
    private Sender sender = new Sender(key);

    private static final Logger LOGGER = LoggerFactory.getLogger(GCMMessageService.class.getName());
    private static final Executor threadPool = Executors.newFixedThreadPool(5);

    /**
     * Sends a message via GCM. Calls asyncSend() to multicast messages asynchronously
     * @param message      The GCM message to send - create using Message.Builder()
     * @throws IOException
     */
    public void sendMessage(Message message) throws IOException {
        List<String> devices = Datastore.getDevices();

        if (devices.isEmpty()) {
            LOGGER.error("Message ignored as there is no device registered!");
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
                    asyncSend(message, partialDevices);
                    partialDevices.clear();
                    tasks++;
                }
            }
            LOGGER.info("Asynchronously sending " + tasks + " multicast messages to " +
                        total + " devices");
        }
    }

    /**
     * Handles the actual dispatch of messages to GCM service. Messages are sent asynchronously
     * using threadpool.execute(). The Datastore is updated if an appplication has changed its
     * regId or is no longer available on the device.
     *
     * @param msg            The GCM message to multicast
     * @param partialDevices The array of devices to receive the message
     */
    private void asyncSend(Message msg, List<String> partialDevices) {

        final List<String> devices = new ArrayList<String>(partialDevices);
        final Message message = msg;

        threadPool.execute(new Runnable() {

            public void run() {
                // Multicast message to 5 devices and collect the results
                MulticastResult multicastResult;
                try {
                    multicastResult = sender.send(message, devices, 5);
                } catch (IOException e) {
                    LOGGER.error("Error posting messages", e);
                    return;
                }
                List<Result> results = multicastResult.getResults();

                for (int i = 0; i < devices.size(); i++) {
                    String regId = devices.get(i);
                    Result result = results.get(i);
                    String messageId = result.getMessageId();
                    if (messageId != null) {
                        // Success: message sent to this device
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
