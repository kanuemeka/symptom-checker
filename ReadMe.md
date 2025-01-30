# Symptom Checker
The Symptom Check Spring Boot API for interview

# Build and Deployment
## Prerequisities
* Java 17 and above
* Maven 3.4 and above
* Docker and Docker Compose (Version 2)

Test and build by running the maven package process
```
mvn clean package
```
Build a docker image of the Spring Boot app
```
docker build -t healthily/symptom-checker:latest .
```

### Docker Compose
Run with Docker Compose
```
docker compose up -d
```
### Database Config
The database tables can be created once the app is running. To create the user table for the application, run:
```
aws dynamodb create-table \
    --table-name user \
    --attribute-definitions \
        AttributeName=userId,AttributeType=S \
        AttributeName=email,AttributeType=S \
    --key-schema \
        AttributeName=userId,KeyType=HASH \
        AttributeName=email,KeyType=RANGE \
    --provisioned-throughput \
        ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --table-class STANDARD \
    --region eu-west-1 \
    --endpoint-url http://localhost:8000
```
And to create the Assessment Table
```
aws dynamodb create-table \
    --table-name assessment \
    --attribute-definitions \
        AttributeName=assessmentId,AttributeType=S \
    --key-schema \
        AttributeName=assessmentId,KeyType=HASH \
    --provisioned-throughput \
        ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --table-class STANDARD \
    --region eu-west-1 \
    --endpoint-url http://localhost:8000
```