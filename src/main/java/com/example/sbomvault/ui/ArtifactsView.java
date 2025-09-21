package com.example.sbomvault.ui;

import com.example.sbomvault.model.Artifact;
import com.example.sbomvault.repo.ArtifactRepo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("artifacts")
@PageTitle("Artifacts")
public class ArtifactsView extends VerticalLayout {
  public ArtifactsView(@Autowired ArtifactRepo repo){
    setSizeFull();
    Grid<Artifact> grid = new Grid<>(Artifact.class, false);
    grid.addColumn(Artifact::getEntityRef).setHeader("EntityRef");
    grid.addColumn(Artifact::getName).setHeader("Name");
    grid.addColumn(Artifact::getVersion).setHeader("Version");
    grid.addColumn(Artifact::getDigest).setHeader("Digest");
    grid.setItems(repo.findAll());
    add(new Button("Refresh", e -> grid.setItems(repo.findAll())), grid);
    expand(grid);
  }
}
