# Spring-boot KStreams exercise
1. Go to ./docker folder and run `docker-compose up -d` command
2. Fix all TODOS
3. Run all tests either with `mvn spring-boot:test` or in IDE
4. Run the app with either with `mvn spring-boot:run` or in IDE
5. observe application's logs std out
6. Go to broker docker image  `docker exec -it broker /bin/bash` 
7. Produce messages with `kafka-console-producer --topic topic_to_consume_from --bootstrap-server localhost:9092 --property "parse.key=true" --property "key.separator=:"`
8. Run command `kafka-topics --list --botstrap-server localhost:9092` and confirm existence of new topics. What are their names?
9. Consume messages from the output topic `kafka-console-consumer --bootstrap-server localhost:9092 --topic ...`
