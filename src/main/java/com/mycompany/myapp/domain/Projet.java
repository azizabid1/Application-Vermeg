package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.Status;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

/**
 * A Projet.
 */
@Entity
@Table(name = "projet")
public class Projet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Type(type = "uuid-char")
    @Column(name = "user_uuid", length = 36, nullable = false)
    private UUID userUuid;

    @Column(name = "nom_projet")
    private String nomProjet;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "technologies")
    private String technologies;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_projet")
    private Status statusProjet;

    @Column(name = "nombre_total")
    private Long nombreTotal;

    @Column(name = "nombre_restant")
    private Long nombreRestant;

    @OneToOne
    @JoinColumn(unique = true)
    private Devis devis;

    @JsonIgnoreProperties(value = { "userId", "votes" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Equipe equipe;

    @ManyToOne
    @JsonIgnoreProperties(value = { "projets" }, allowSetters = true)
    private Tache tache;

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

    public UUID getUserUuid() {
        return this.userUuid;
    }

    public Projet userUuid(UUID userUuid) {
        this.setUserUuid(userUuid);
        return this;
    }

    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
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

    public Long getNombreTotal() {
        return this.nombreTotal;
    }

    public Projet nombreTotal(Long nombreTotal) {
        this.setNombreTotal(nombreTotal);
        return this;
    }

    public void setNombreTotal(Long nombreTotal) {
        this.nombreTotal = nombreTotal;
    }

    public Long getNombreRestant() {
        return this.nombreRestant;
    }

    public Projet nombreRestant(Long nombreRestant) {
        this.setNombreRestant(nombreRestant);
        return this;
    }

    public void setNombreRestant(Long nombreRestant) {
        this.nombreRestant = nombreRestant;
    }

    public Devis getDevis() {
        return this.devis;
    }

    public void setDevis(Devis devis) {
        this.devis = devis;
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
    }

    public Projet tache(Tache tache) {
        this.setTache(tache);
        return this;
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
            ", userUuid='" + getUserUuid() + "'" +
            ", nomProjet='" + getNomProjet() + "'" +
            ", dateDebut='" + getDateDebut() + "'" +
            ", dateFin='" + getDateFin() + "'" +
            ", technologies='" + getTechnologies() + "'" +
            ", statusProjet='" + getStatusProjet() + "'" +
            ", nombreTotal=" + getNombreTotal() +
            ", nombreRestant=" + getNombreRestant() +
            "}";
    }
}
