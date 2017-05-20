

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


