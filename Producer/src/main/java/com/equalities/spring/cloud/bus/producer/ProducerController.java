package com.equalities.spring.cloud.bus.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.equalities.spring.cloud.bus.producer.events.CustomEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ProducerController {

  @Value("${spring.cloud.bus.id}")
  String busId;
  
  @Autowired
  ApplicationContext context;

  @GetMapping("/send/{message}")
  String produceEvent(@PathVariable String message) {
    log.info("Bus ID: {}", busId);
    
    final CustomEvent event = new CustomEvent(this, busId, message);
    context.publishEvent(event);
    
    log.info("Event sent!");

    return "Sent event with message '" + message + "'.<br>"; 
  }
}
