package com.example.flowAggregationService;

import java.text.MessageFormat;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlowAggregationController {

  private static final String ALL_SUCCESS = "All Success: {0}";
  private static final String FAILED_SOME = "Failed Count: {0}";
  private final FlowStorageRepository repository;

  public FlowAggregationController(FlowStorageRepository repository) {
    this.repository = repository;
  }

  /**
   * The endpoint serving GET requests
   *
   * @param hour The value for the hour you want to look up. Valid hour values are integers starting
   * from 1.
   * @return a json array containing aggregated bytes_tx and bytes_ry for every (src_app + dest_app
   * + vpc_id + hour) tuple
   */
  @GetMapping(path = "/flows", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> get(@RequestParam(value = "hour", defaultValue = "1") String hour) {
    List<NetFlowEntity> body = repository.get(hour);
    return ResponseEntity.ok(body);
  }

  /**
   * The endpoint serving POST requests
   *
   * @return TODO Return current size of hourly buckets
   * @requestBody a json array containing (src_app, dest_app, vpc_id, bytes_tx, bytes_rx, hour)
   * tuples
   */
  @PostMapping(path = "/flows",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity post(@RequestBody final List<NetFlowEntity> netFlowEntities) {
    int totalToBeUpdated = netFlowEntities.size();
    List<NetFlowEntity> body = repository.save(netFlowEntities);
    if (body.isEmpty()) {
      return ResponseEntity.ok().body(MessageFormat.format(ALL_SUCCESS, totalToBeUpdated, 0));
    } else {
      return ResponseEntity.ok()
          .body(MessageFormat.format(FAILED_SOME, body.size()));
    }
  }
}