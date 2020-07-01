package com.equalities.spring.cloud.bus.producer.events;

import org.springframework.cloud.bus.event.RemoteApplicationEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
      // See Spring Cloud Bus documentation for the syntax.
      super(source, originService, destinationService);
      this.message = message;
  }
}