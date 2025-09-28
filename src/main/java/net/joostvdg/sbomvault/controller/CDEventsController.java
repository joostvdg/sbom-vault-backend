/* (C)2025 */
package net.joostvdg.sbomvault.controller;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import net.joostvdg.sbomvault.service.CDEventsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cdevents")
public class CDEventsController {

  private final CDEventsService svc;

  public CDEventsController(CDEventsService svc) {
    this.svc = svc;
  }

  @PostMapping(consumes = "application/cloudevents+json")
  public ResponseEntity<?> receiveStructured(@RequestBody JsonNode cloudEvent) {
    svc.handleStructured(cloudEvent);
    return ResponseEntity.accepted().build();
  }

  @PostMapping
  public ResponseEntity<?> receiveBinary(
      @RequestHeader Map<String, String> headers, @RequestBody(required = false) byte[] body) {
    svc.handleBinary(headers, body);
    return ResponseEntity.accepted().build();
  }
}
