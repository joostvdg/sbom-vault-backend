package com.example.sbomvault.service;

import com.example.sbomvault.model.Artifact;
import com.example.sbomvault.repo.ArtifactRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class CDEventsService {
  private final ObjectMapper om = new ObjectMapper();
  private final ArtifactRepo repo;

  public CDEventsService(ArtifactRepo repo){ this.repo = repo; }

  @Transactional
  public void handleStructured(JsonNode cloudEvent){
    // Expect CloudEvents v1.0 envelope; extract 'type' and 'data'
    String type = cloudEvent.path("type").asText("");
    JsonNode data = cloudEvent.path("data");
    process(type, data);
  }

  @Transactional
  public void handleBinary(Map<String,String> headers, byte[] body){
    String type = headers.getOrDefault("ce-type", "");
    JsonNode data = om.nullNode();
    if(body != null && body.length > 0){
      try { data = om.readTree(new String(body, StandardCharsets.UTF_8)); } catch (Exception ignored) {}
    }
    process(type, data);
  }

  private void process(String type, JsonNode data){
    // Minimal demo: upsert artifact on artifact.* events
    if(type.startsWith("dev.cdevents.artifact.")){
      String digest = data.path("artifact").path("digest").asText(null);
      String name = data.path("artifact").path("name").asText("unknown");
      String entityRef = data.path("producer").path("component").asText("component:default/unknown");
      Artifact a = repo.findByDigest(digest).orElseGet(Artifact::new);
      a.setDigest(digest);
      a.setName(name);
      a.setKind(data.path("artifact").path("kind").asText("oci"));
      a.setEntityRef(entityRef);
      a.setUri(data.path("artifact").path("uri").asText(null));
      repo.save(a);
    }
    // Extend here for build.finished/pipeline.run.finished, etc.
  }
}
