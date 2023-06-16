package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.Status;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Tache} entity.
 */
public class TacheDTO implements Serializable {

    private Long id;

    private String titre;

    private String description;

    private Status statusTache;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatusTache() {
        return statusTache;
    }

    public void setStatusTache(Status statusTache) {
        this.statusTache = statusTache;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TacheDTO)) {
            return false;
        }

        TacheDTO tacheDTO = (TacheDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tacheDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TacheDTO{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", description='" + getDescription() + "'" +
            ", statusTache='" + getStatusTache() + "'" +
            "}";
    }
}
