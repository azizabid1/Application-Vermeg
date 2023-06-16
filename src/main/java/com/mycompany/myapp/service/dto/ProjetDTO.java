package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.Status;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Projet} entity.
 */
public class ProjetDTO implements Serializable {

    private Long id;

    private String nomProjet;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private String technologies;

    private Status statusProjet;

    private DevisDTO devis;

    private EquipeDTO equipe;

    private TacheDTO tache;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomProjet() {
        return nomProjet;
    }

    public void setNomProjet(String nomProjet) {
        this.nomProjet = nomProjet;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getTechnologies() {
        return technologies;
    }

    public void setTechnologies(String technologies) {
        this.technologies = technologies;
    }

    public Status getStatusProjet() {
        return statusProjet;
    }

    public void setStatusProjet(Status statusProjet) {
        this.statusProjet = statusProjet;
    }

    public DevisDTO getDevis() {
        return devis;
    }

    public void setDevis(DevisDTO devis) {
        this.devis = devis;
    }

    public EquipeDTO getEquipe() {
        return equipe;
    }

    public void setEquipe(EquipeDTO equipe) {
        this.equipe = equipe;
    }

    public TacheDTO getTache() {
        return tache;
    }

    public void setTache(TacheDTO tache) {
        this.tache = tache;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjetDTO)) {
            return false;
        }

        ProjetDTO projetDTO = (ProjetDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projetDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjetDTO{" +
            "id=" + getId() +
            ", nomProjet='" + getNomProjet() + "'" +
            ", dateDebut='" + getDateDebut() + "'" +
            ", dateFin='" + getDateFin() + "'" +
            ", technologies='" + getTechnologies() + "'" +
            ", statusProjet='" + getStatusProjet() + "'" +
            ", devis=" + getDevis() +
            ", equipe=" + getEquipe() +
            ", tache=" + getTache() +
            "}";
    }
}
