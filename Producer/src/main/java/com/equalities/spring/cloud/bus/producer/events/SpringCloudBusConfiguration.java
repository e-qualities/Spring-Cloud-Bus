package com.equalities.spring.cloud.bus.producer.events;

import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@RemoteApplicationEventScan
public class SpringCloudBusConfiguration {
    // Empty on purpose. This configuration serves as a marker for Spring Boot that
    // the package containing this class should be scanned for custom
    // RemoteApplicationEvent types.
    // Don't move this class unless you are moving the event types with it.
}
