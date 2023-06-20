package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.Status;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Tache.
 */
@Entity
@Table(name = "tache")
public class Tache implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "titre")
    private String titre;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_tache")
    private Status statusTache;

    @ManyToMany(mappedBy = "taches")
    @JsonIgnoreProperties(value = { "equipe", "taches", "devis" }, allowSetters = true)
    private Set<Projet> projets = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tache id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return this.titre;
    }

    public Tache titre(String titre) {
        this.setTitre(titre);
        return this;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return this.description;
    }

    public Tache description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatusTache() {
        return this.statusTache;
    }

    public Tache statusTache(Status statusTache) {
        this.setStatusTache(statusTache);
        return this;
    }

    public void setStatusTache(Status statusTache) {
        this.statusTache = statusTache;
    }

    public Set<Projet> getProjets() {
        return this.projets;
    }

    public void setProjets(Set<Projet> projets) {
        if (this.projets != null) {
            this.projets.forEach(i -> i.removeTaches(this));
        }
        if (projets != null) {
            projets.forEach(i -> i.addTaches(this));
        }
        this.projets = projets;
    }

    public Tache projets(Set<Projet> projets) {
        this.setProjets(projets);
        return this;
    }

    public Tache addProjet(Projet projet) {
        this.projets.add(projet);
        projet.getTaches().add(this);
        return this;
    }

    public Tache removeProjet(Projet projet) {
        this.projets.remove(projet);
        projet.getTaches().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tache)) {
            return false;
        }
        return id != null && id.equals(((Tache) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tache{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", description='" + getDescription() + "'" +
            ", statusTache='" + getStatusTache() + "'" +
            "}";
    }
}
