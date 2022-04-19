package com.example.flowAggregationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FlowAggregationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FlowAggregationService.class);

  public static void main(String[] args) {
    SpringApplication.run(FlowAggregationService.class, args);
    LOGGER.info("Simple SpringApplication for netflow aggregation started");
  }

}
