# Kotlin Bank
Banking software written in Kotlin. Uses CQRS and EventSourcing.

## Dependencies
There are only two outside of this project. Mongo and EventStoreDB.

### Starting Mongo with Docker
```
docker run --name some-mongo -p "27017:27017" mongo:latest
```

### Starting EventStoreDB with Docker
```
docker run -it -p 2113:2113 -p 1113:1113 \
    ghcr.io/eventstore/eventstore:20.6.1-alpha.0.69-arm64v8 --insecure --run-projections=All \
    --enable-external-tcp --enable-atom-pub-over-http
```

## API Server
The API accepts JSON and returns JSON. All commands and queries can be found in the postman collection.
