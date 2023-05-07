# JCPM Core Application

This is the core application for Jewelry Components and Products Management (JCPM).
It is responsible for storing and managing items, and all the related business logic.

The following API endpoints can be called by clients:
* ```/api/v1/product``` for getting and adding products (GET, POST methods)
* ```/api/v1/product/{product_id}``` for getting, updating, and deleting info about 
a certain product (GET, PATCH, DELETE methods)
* (more endpoints to be implemented)

See detailed and up-to-date OpenAPI documentation in [Swagger](http://localhost:8080/api/v1/docs/swagger.html). 