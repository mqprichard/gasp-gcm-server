Gasp! GCM Push Notification Server
----------------------------------

Push data synchronization server for Gasp! Android demo: uses CloudBees PaaS and Foxweave to provide automatic data sync between the Gasp! server database and Android SQLite on-device data stores. This version shows a standalone server app, with data sync triggered by a FoxWeave Integration Pipeline: the next version will be implemented as a FoxWeave Connector, obviating the need for a separate server deployment.

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

3. Build with: `mvn build install`

4. Deploy to CloudBees: `bees app:deploy -a gasp-gcm-server target/gasp-gcm-server.war -P GCM_API_KEY=<your API key>`

5. Configure a FoxWeave Integration (Sync) App 

6. Add a pipeline for each table {review, restaurant, user}:
   - Source: MySQL 5 (pointing at your CloudBees MySQL Gasp database)
   - Table/View = {restaurant | review | user}
   - Add data filter rule: "id greater than last sync value"
   - Target: CloudBees App
   - App Name: gasp-gcm-server  App Event: {Review | Restaurant | User} 
   - Data Mapping: `id->${id}, ...` etc for each JSON field

7. Deploy your FoxWeave Integration App on CloudBees and start it

Viewing the Server Log
----------------------

You can view the server log using `bees app:tail -a gasp-gcm-server`
