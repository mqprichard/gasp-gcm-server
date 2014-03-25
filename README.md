Gasp! GCM Push Notification Server
----------------------------------

Push data synchronization server for Gasp! Android demo, using CloudBees PaaS and Foxweave to provide automatic data sync between the Gasp! server database and Android SQLite on-device data stores.

The server uses [Google Cloud Messaging for Android](http://developer.android.com/google/gcm/index.html) to multicast Gasp! database updates to Android applications that register with the gasp-gcm-server application via simple HTTP calls. The [FoxWeave Integration](http://developer.cloudbees.com/bin/view/FoxWeave/App-Centric+Integrations+for+RUN%40cloud+Apps) service will call via WebHook the REST API exposed by DataSyncService.java, which it turn multicasts a GCM notification with the record id to registered devices.  The Android client can then call the Gasp! REST API to retrieve the review data and update its on-device database (see the [gasp-gcm-client](https://github.com/mqprichard/gasp-gcm-client) project for an example of how this works). The FoxWeave integration is currently set to poll the target database every 10 secs.

There is an example Android demo client project [gasp-gcm-client](https://github.com/mqprichard/gasp-gcm-client) on GitHub. Make sure that your client uses the Google APIs Project Number that corresponds to the API key used to run this server application (You can check this using the [Google APIs Console](https://cloud.google.com/console)). 

Setup
-----

1. Set up the Gasp! server and database: see [gasp-server](https://github.com/cloudbees/gasp-server) on GitHub

2. Configure Google APIs for Google Cloud Messaging
   - Logon to [Google APIs Console](https://code.google.com/apis/console)
   - Services -> Google Cloud Messaging for Android = ON
   - API Access -> Simple API Access -> Key for server apps (note API Key)
   - Overview (note 12-digit Project Number for Android client)


3. Build with:

   `mvn build install`

4. Deploy to CloudBees:

   `bees app:deploy -a gasp-gcm-server target/gasp-gcm-server.war -P GCM_API_KEY=<your API key>`

5. To send a GCM update message using curl and scripts from [gasp-scripts](https://github.com/mqprichard/gasp-scripts):
   - Create a review with addReview and note Location header in response:

   `curl -i -H Content-Type:application/json -X POST http://gasp2.partnerdemo.cloudbees.net/reviews -d {"star":5,"comment":"Nice","user":"http://gasp2.partnerdemo.cloudbees.net/users/1","restaurant":"http://gasp2.partnerdemo.cloudbees.net/restaurants/136"}`

   `Location: http://gasp2.partnerdemo.cloudbees.net/reviews/432`

   - Use getReview for JSON-formatted review data, e.g.

   `curl -i http://gasp2.partnerdemo.cloudbees.net/reviews/432`

   `{"star":5,"comment":"Nice","id":432,"url":"/reviews/432","restaurant":"/restaurants/136","user":"/users/1"}`

   - Call the /review/created REST endpoint on gaso-gcm-server, e.g.

   `curl http://gasp-gcm-server.partnerdemo.cloudbees.net/review/created -H "Content-Type:application/json" -d '{"star":5,"comment":"Nice","id":432,"url":"/reviews/432","restaurant":"/restaurants/136","user":"/users/1"}'`

   - View the update in the gasp-gcm-server log

   `bees app:tail -a gasp-gcm-server`

   `INFO  DataSyncService - Syncing Review Id: 432`
