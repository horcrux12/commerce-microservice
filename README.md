# How to run this application
run docker compose to install PostgreSQL and MongoDB with command 
```bash
docker compose up -d
```

Run discovery-service for activate API Gateway for all services and then run product service, inventory service, and product service

## service contracts
- Product Service : Create and View Products, acts as Product Catalogue
- Order Service : Can Order Product
- Inventory Service : Can check id product is in stock or not.
- Order Service, Inventory Service, and Notification Service are going to interact with each other with Sync and Async Communication.