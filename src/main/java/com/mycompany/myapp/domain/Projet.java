package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.Status;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Projet.
 */
@Table("projet")
public class Projet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nom_projet")
    private String nomProjet;

    @Column("date_debut")
    private LocalDate dateDebut;

    @Column("date_fin")
    private LocalDate dateFin;

    @Column("technologies")
    private String technologies;

    @Column("status_projet")
    private Status statusProjet;

    @Transient
    private Devis devis;

    @Transient
    private Equipe equipe;

    @Transient
    @JsonIgnoreProperties(value = { "projets" }, allowSetters = true)
    private Tache tache;

    @Column("devis_id")
    private Long devisId;

    @Column("equipe_id")
    private Long equipeId;

    @Column("tache_id")
    private Long tacheId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Projet id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomProjet() {
        return this.nomProjet;
    }

    public Projet nomProjet(String nomProjet) {
        this.setNomProjet(nomProjet);
        return this;
    }

    public void setNomProjet(String nomProjet) {
        this.nomProjet = nomProjet;
    }

    public LocalDate getDateDebut() {
        return this.dateDebut;
    }

    public Projet dateDebut(LocalDate dateDebut) {
        this.setDateDebut(dateDebut);
        return this;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return this.dateFin;
    }

    public Projet dateFin(LocalDate dateFin) {
        this.setDateFin(dateFin);
        return this;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getTechnologies() {
        return this.technologies;
    }

    public Projet technologies(String technologies) {
        this.setTechnologies(technologies);
        return this;
    }

    public void setTechnologies(String technologies) {
        this.technologies = technologies;
    }

    public Status getStatusProjet() {
        return this.statusProjet;
    }

    public Projet statusProjet(Status statusProjet) {
        this.setStatusProjet(statusProjet);
        return this;
    }

    public void setStatusProjet(Status statusProjet) {
        this.statusProjet = statusProjet;
    }

    public Devis getDevis() {
        return this.devis;
    }

    public void setDevis(Devis devis) {
        this.devis = devis;
        this.devisId = devis != null ? devis.getId() : null;
    }

    public Projet devis(Devis devis) {
        this.setDevis(devis);
        return this;
    }

    public Equipe getEquipe() {
        return this.equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
        this.equipeId = equipe != null ? equipe.getId() : null;
    }

    public Projet equipe(Equipe equipe) {
        this.setEquipe(equipe);
        return this;
    }

    public Tache getTache() {
        return this.tache;
    }

    public void setTache(Tache tache) {
        this.tache = tache;
        this.tacheId = tache != null ? tache.getId() : null;
    }

    public Projet tache(Tache tache) {
        this.setTache(tache);
        return this;
    }

    public Long getDevisId() {
        return this.devisId;
    }

    public void setDevisId(Long devis) {
        this.devisId = devis;
    }

    public Long getEquipeId() {
        return this.equipeId;
    }

    public void setEquipeId(Long equipe) {
        this.equipeId = equipe;
    }

    public Long getTacheId() {
        return this.tacheId;
    }

    public void setTacheId(Long tache) {
        this.tacheId = tache;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Projet)) {
            return false;
        }
        return id != null && id.equals(((Projet) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Projet{" +
            "id=" + getId() +
            ", nomProjet='" + getNomProjet() + "'" +
            ", dateDebut='" + getDateDebut() + "'" +
            ", dateFin='" + getDateFin() + "'" +
            ", technologies='" + getTechnologies() + "'" +
            ", statusProjet='" + getStatusProjet() + "'" +
            "}";
    }
}
