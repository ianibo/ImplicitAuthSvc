

A microservice that supports the implicit grant flow in a Kong environment.

Contains https://github.com/yury-dymov/redux-oauth-client-demo.git - A sample react/redux oauth client


Overview

# ImplicitAuthService 

A grails 3 app intended to be delivere as a microservice which provides an endpoint at /auth/${provider}/authorize

The app will be called by client code which has intercepted a redirect containing an authorization token. This token will be stripped out
and then appended to this URL endpoint. It is the task of the ImplicitAuthService to validate that authorization response and provide a JWT token
back to the client which can then be validated by the Kong JWT infrastructure.


# demoSPA

Essentially https://github.com/yury-dymov/redux-oauth-client-demo.git configured to talk to a Kong api gateway running on localhost and providing ImplicitAuthService endpoints

Build/Running



# DB Setup

If you are running postgres inside a docker container, use

psql -h localhost -U postgres
-- You will have set a PGSql Password at install - change it from any default!

If you are running postgres locally sudo su - postgres

Once connected, issue

CREATE USER knowint WITH PASSWORD 'knowint';
CREATE DATABASE knowint;
GRANT ALL PRIVILEGES ON DATABASE knowint to knowint;

CREATE USER authsvc WITH PASSWORD 'authsvc';
CREATE SCHEMA authsvc;
GRANT ALL PRIVILEGES ON SCHEMA authsvc to authsvc;
ALTER USER authsvc set search_path = 'authsvc','public';

# For demo service
CREATE USER demosvc WITH PASSWORD 'demosvc';
CREATE SCHEMA demosvc;
GRANT ALL PRIVILEGES ON SCHEMA authsvc to authsvc;
GRANT ALL PRIVILEGES ON SCHEMA demosvc to demosvc;
ALTER USER demosvc set search_path = 'demosvc', 'authsvc','public';



-- This setup will create a database called knowint with a schema called authsvc. Other user accounts will be able to be created
in the same database but different schemas - allowing visibility of the schema if needed. If is anticipated that other service components,
for example "svc1" will do something like

CREATE USER svc1 WITH PASSWORD 'svc1';
CREATE SCHEMA svc1;
GRANT ALL PRIVILEGES ON SCHEMA authsvc to svc1;
GRANT ALL PRIVILEGES ON SCHEMA svc1 to svc1;
ALTER USER authsvc set search_path = 'svc1', 'authsvc','public';

To allow svc1 to see it's own tables, and the auth tables. Obviously, the grant might be read only or restricted to specific tables

DB Session usually checked with

psql -h localhost -U authsvc knowint

# Installing authorization services in the database

insert into oauth_authorization_service(code,base_url,client_id,redirect_url,response_type,scope) 
values ( "google", "https://accounts.google.com/o/oauth2/v2/auth", 
"452432600734-gg239i5odhfki1lhpt7c01pfir3267ol.apps.googleusercontent.com", 
"http://localhost:8080/authorize/google", 
"token", 
"https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile"); 


Some info on how to share tables between schemas::

https://stackoverflow.com/questions/8283474/how-to-share-a-table-between-multiple-postgresql-databases
