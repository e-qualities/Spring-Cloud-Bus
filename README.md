# Spring-Cloud-Bus

In this branch we show the usage of Spring Cloud Bus. The sample features:

* Solace PubSub+ as the message broker middleware
* Event Producer exposing a simple REST API to fire events
* Event Consumer listening for events
* Custom event types defined and sent via Spring Cloud Bus

> Note: In this sample we use Solace PubSub+ as the broker middleware. Thanks to the usage of Spring Cloud Stream (which is underlying to Spring Cloud Bus) this could be easily exchanged for Kafka, RabbitMQ, Amazon Kinesis or even other brokers.

Here a short introduction presented on Solace's Lightning Talks. You can find the slides in the [assets](./assets) folder.
<iframe width="560" height="315" src="https://www.youtube.com/embed/sKPPGb75s4c?start=517" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>

# Building the Project

To build this project simply execute `mvn clean package` on the root of this project.

# Project Structure

The project structure is very simple:

* `Producer` - Contains the code that produces a message and broadcasts it via Spring Cloud Bus. The Producer runs on port `8001` and exposes a very simple REST API to produce events. You can access the API under `http://localhost:8001/send/<yourmessage>`.
* `Consumer` - Contains the code that consumes the event sent via Spring Cloud Bus. The consumer runs on port `8002` and prints any received events to the console.
* `scripts` - contains two simple shell scripts that start and stop Solace PubSub+ locally from a docker container. You need to have docker installed to run these scripts.

# Running the Project

`Producer` and `Consumer` both rely on Solace PubSub+ as the message broker middleware. Hence the broker needs to be started before `Consumer` and `Producer` are.

Proceed as follows:
1. Build the project
2. Start the broker by executing `./scripts/startSolace.sh`
3. Start the `Producer` by executing `mvn -f ./Producer/pom.xml spring-boot:run`
4. Start the `Consumer` by executing `mvn -f ./Consumer/pom.xml spring-boot:run`

Once all is up and running the following endpoints will be available:
* Solace PubSub+ Web interface: http://localhost:8080/
* Producer REST endpoint: http://localhost:8001/send/{your message}

To try out the sample, open your browser and point it to http://localhost:8001/send/HelloWorld.

As a result you should see console output on the `Producer` like this:

```
2020-07-01 18:33:09.512  INFO 35203 --- [nio-8001-exec-1] c.e.s.c.bus.producer.ProducerController  : Event sent!
```

... and on the `Consumer` side like that:

```
2020-07-01 18:33:09.558  INFO 35204 --- [sumerDispatcher] c.e.s.cloud.bus.consumer.EventConsumer   : Received the event!
2020-07-01 18:33:09.558  INFO 35204 --- [sumerDispatcher] c.e.s.cloud.bus.consumer.EventConsumer   : -- Event Message: HelloWorld
```

Feel free to inspect the code for details.

# How Custom Events Work

Spring Cloud Bus is based on Spring Cloud Stream, and is a lightweight message bus.
Spring Cloud Bus uses Spring's `ApplicationEvent` mechanism to send and receive events from an application.
Normally, Spring's `ApplicationEvent` mechanism is used to send and receive events within the same application, and often this mechanism is used inside an application to get informed about lifecycle events of the Spring application. An `ApplicationEvent` is sent via Spring `ApplicationContext`'s `publishEvent()` method and usually received by implementing an `ApplicationEventListener` or by annotating an event listener method with the `@EventListener` annotation.

Spring Cloud Bus introduces a custom `ApplicationEventListener` under the hood that intercepts these application-local events (sent via the Spring application context) and - if they are targeted for a remote application - sends them out via Spring Cloud Stream to a message broker middleware (like Solace PubSub+ or RabbitMQ).
To distinguish "local" events from those targeted for remote applications, Spring Cloud Bus introduced the `RemoteApplicationEvent` - a type derived from `ApplicationEvent` and intended as superclass for event types that are to be sent _between_ applications and via Spring Cloud Bus.

The custom event shown in this sample, simply derives from `RemoteApplicationEvent` and looks as follows:

```java
@SuppressWarnings("serial")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class CustomEvent extends RemoteApplicationEvent {
    private String message;

    // Must supply a default constructor and getters/setters for deserialization
    public CustomEvent() {
    }

    public CustomEvent(Object source, String originService, String message) {
        // Source is the object that is publishing the event
        // OriginService is the unique context ID of the publisher
        super(source, originService);
        this.message = message;
    }
    
    public CustomEvent(Object source, String originService, String message, String destinationService) {
      // Source is the object that is publishing the event
      // OriginService is the unique context ID of the publisher
      // Destination service is a the bus address of the service the event is targeted for.
      // See Spring Cloud Bus documentation for the syntax of the addressing scheme.
      super(source, originService, destinationService);
      this.message = message;
  }
}
```

For Spring Cloud Bus to be aware of this custom event, you need to add the `@RemoteApplicationEventScan` annotation to your Spring Boot application.
You can specify a list of packages that Spring Cloud Bus will scan for `RemoteApplicationEvents`. If you specify no package list, the package that includes a configuration with the given annotation will be searched for events. We use this mechanism, and simply place an empty `@Configuration` annotated with `@RemoteApplicationEventScan` into the package where we will define our custom event types like so:

```java
// All events in this package will be picked up by Spring Cloud Bus!
package com.equalities.spring.cloud.bus.producer.events;

import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@RemoteApplicationEventScan
public class SpringCloudBusConfiguration {
    // Empty on purpose. This configuration serves as a marker for Spring Boot that
    // the package containing this configuration should be scanned for custom
    // RemoteApplicationEvent types.
    // Don't move this class unless you are moving the event types with it.
}
```

# Using a Different Broker Middleware

Spring Cloud Bus uses Spring Cloud Stream which provides an abstraction over a variety of common messaging infrastructure.
Most notably Spring Cloud Stream supports RabbitMQ, Kafka, Amazon Kinesis, Solace PubSub+, Google PubSub and others.

In this sample we use Solace PubSub+ as the broker middleware - simply because it is less common to find a sample that shows its usage.
However, the code shown here does not depend on Solace PubSub+ at all. In fact you can easily use the same sample with a RabbitMQ or Kafka instance.
All you need to do is replace the Solace PubSub+ dependency in `pom.xml` for that of RabbitMQ or Kafka.

E.g. you could replace:

```xml
<dependency>
  <groupId>com.solace.spring.cloud</groupId>
  <artifactId>spring-cloud-starter-stream-solace</artifactId>
  <version>${spring.cloud.streams.solace.binder.version}</version>
</dependency>
```
... with the following dependency to run against RabbitMQ:

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-stream-binder-rabbit</artifactId>
</dependency>
```

Of course, you would then have to start a RabbitMQ broker from a docker image locally instead of the Solace PubSub+ instance.


# References

* [Spring Cloud Bus](https://spring.io/projects/spring-cloud-bus)
* [Spring Cloud Stream](https://spring.io/projects/spring-cloud-stream)
* [Spring Cloud Bus Addressing Scheme](https://cloud.spring.io/spring-cloud-static/spring-cloud-bus/2.2.2.RELEASE/reference/html/#addressing-an-instance)
