Gasp! GCM for Android Demo Server
==================================================

Push data synchronization server for Gasp! Android demo: uses CloudBees PaaS and Foxweave to provide automatic data sync between Gasp! server and Android SQLite on-device data store. This version shows a standalone server app, with data sync triggered by a FoxWeave Integration Pipeline: the next version will be implemented as a FoxWeave Connector, obviating the need for a separate server deployment.

Pre-reqs
--------

Set up the Gasp! server and database [gasp-server](https://github.com/cloudbees/gasp-server)
Configure a FoxWeave Integration (Sync) App with a pipeline as follows:
Source: MySQL 5 (pointing at your gasp-db)
SQL Statement: select #id, #comment, #star, #restaurant_id, #user_id from review where id > ##id
Target: WebHook
Target URL: http://gasp-gcm-server.<cloudbees_user>.cloudbees.net
JSON Message Structure:
....
{
    "id":1, 
    "comment":"blank", 
    "star":"three", 
    "restaurant_id":1, 
    "user_id":1
}
....
Data Mapping: id->${id}, comment->${comment} etc

Build with: mvn build install
Deploy with bees app:deploy -a gasp-gcm-server target/gasp-gcm-server.war 
