Gasp! GCM for Android Demo Server
==================================================

Push data synchronization server for Gasp! Android demo: uses CloudBees PaaS and Foxweave to provide automatic data sync between the Gasp! server database and Android SQLite on-device data stores. This version shows a standalone server app, with data sync triggered by a FoxWeave Integration Pipeline: the next version will be implemented as a FoxWeave Connector, obviating the need for a separate server deployment.

The server uses [Google Cloud Messaging for Android](http://developer.android.com/google/gcm/index.html) to multicast Gasp! database updates to Android applications that register with the gasp-gcm-server application via simple HTTP calls (see GCMRegistration.java). The FoxWeave Integration service will call via WebHook the REST API exposed by DataSyncService.java, which it turn multicasts a GCM notification with the record id to registered devices.  The Android client can then call the Gasp! REST API to retrieve the review data and update its on-device database (see the gasp-gcm-client project for an example of how this works). The FoxWeave integration is currently set to poll the target database every minute, so for testing purposes you may want to trigger the pipeline manually to see the update notifications immediately.

There is an example Android demo client project [gasp-gcm-client](https://github.com/mqprichard/gasp-gcm-client) on GitHub. You will need to make sure that you use the Google APIs Project Number that corresponds to the API key used to run this server application. 

Setup
-----

1. Set up the Gasp! server and database: see [gasp-server](https://github.com/cloudbees/gasp-server) on GitHub

2. Configure a FoxWeave Integration (Sync) App with a pipeline as follows:
   - Source: MySQL 5 (pointing at your CloudBees MySQL Gasp database)
   - SQL Statement: select #id, #comment, #star, #restaurant_id, #user_id from review where id > ##id
   - Target: WebHook
   - Target URL: http://gasp-gcm-server.<cloudbees_user>.cloudbees.net
   - JSON Message Structure:
`{
    "id":1, 
    "comment":"blank", 
    "star":"three", 
    "restaurant_id":1, 
    "user_id":1
}`
   - Data Mapping: `id->${id}, comment->${comment}` etc

3. Configure Google APIs for Google Cloud Messaging
   - Logon to [Google APIs Console](https://code.google.com/apis/console)
   - Services -> Google Cloud Messaging for Android = ON
   - API Access -> Simple API Access -> Key for server apps (note API Key)
   - Overview (note 12-digit Project Number for Android client)

4. Deploy your FoxWeave Integration App on CloudBees and start it

5. Build with: `mvn build install`

6. Deploy to CloudBees: `bees app:deploy -a gasp-gcm-server target/gasp-gcm-server.war -P GCM_API_KEY=<your API key>`

Viewing the Server Log
----------------------

You can view the server log using `bees app:tail -a gasp-gcm-server`
