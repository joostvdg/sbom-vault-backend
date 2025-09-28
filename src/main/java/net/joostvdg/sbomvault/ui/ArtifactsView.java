/* (C)2025 */
package net.joostvdg.sbomvault.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.joostvdg.sbomvault.model.Artifact;
import net.joostvdg.sbomvault.repo.ArtifactRepo;
import org.springframework.beans.factory.annotation.Autowired;

@Route("artifacts")
@PageTitle("Artifacts")
public class ArtifactsView extends VerticalLayout {
  public ArtifactsView(@Autowired ArtifactRepo repo) {
    setSizeFull();
    Grid<Artifact> grid = new Grid<>(Artifact.class, false);
    // Replace direct entityRef access with access through the catalogEntity relationship
    grid.addColumn(
            artifact ->
                artifact.getCatalogEntity() != null
                    ? artifact.getCatalogEntity().getEntityRef()
                    : "")
        .setHeader("EntityRef");
    grid.addColumn(Artifact::getName).setHeader("Name");
    grid.addColumn(Artifact::getVersion).setHeader("Version");
    grid.addColumn(Artifact::getDigest).setHeader("Digest");
    grid.setItems(repo.findAll());
    add(new Button("Refresh", e -> grid.setItems(repo.findAll())), grid);
    expand(grid);
  }
}
