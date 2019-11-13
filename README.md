# ignite-cassandra-cache

Single node Apache Ignite node set up with Apache Cassandra in write-through and read-through mode.

## Usage
To build project, run tests, build, and start Docker image use this command:
```bash
./gradlew clean shadowJar runDockerImage
```

NOTE: This does not start Cassandra instance. Find how to start Cassandra in Docker [here](https://hub.docker.com/_/cassandra).

## Configuration
All settings are at `/src/main/resources/`. To specify Cassandra hostname, change `cassandra.contactPoints` at `ignite-cassandra.properties` file.

Find more information in [Ignite Docs](https://apacheignite-mix.readme.io/docs/ignite-with-apache-cassandra).

