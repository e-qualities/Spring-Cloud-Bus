package com.equalities.spring.cloud.bus.consumer;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.equalities.spring.cloud.bus.consumer.events.CustomEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EventConsumer {

  @EventListener
  protected void onSpringCloudBusEvent(CustomEvent event) {
    log.info("Received the event!");
    log.info("-- Event Message: {}", event.getMessage());
  }
}
