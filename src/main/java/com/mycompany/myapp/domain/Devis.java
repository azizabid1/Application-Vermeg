package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * not an ignored comment
 */
@Table("devis")
public class Devis implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("prix_total")
    private Double prixTotal;

    @Column("prix_ht")
    private Double prixHT;

    @Column("prix_service")
    private Double prixService;

    @Column("duree_projet")
    private LocalDate dureeProjet;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Devis id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrixTotal() {
        return this.prixTotal;
    }

    public Devis prixTotal(Double prixTotal) {
        this.setPrixTotal(prixTotal);
        return this;
    }

    public void setPrixTotal(Double prixTotal) {
        this.prixTotal = prixTotal;
    }

    public Double getPrixHT() {
        return this.prixHT;
    }

    public Devis prixHT(Double prixHT) {
        this.setPrixHT(prixHT);
        return this;
    }

    public void setPrixHT(Double prixHT) {
        this.prixHT = prixHT;
    }

    public Double getPrixService() {
        return this.prixService;
    }

    public Devis prixService(Double prixService) {
        this.setPrixService(prixService);
        return this;
    }

    public void setPrixService(Double prixService) {
        this.prixService = prixService;
    }

    public LocalDate getDureeProjet() {
        return this.dureeProjet;
    }

    public Devis dureeProjet(LocalDate dureeProjet) {
        this.setDureeProjet(dureeProjet);
        return this;
    }

    public void setDureeProjet(LocalDate dureeProjet) {
        this.dureeProjet = dureeProjet;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Devis)) {
            return false;
        }
        return id != null && id.equals(((Devis) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Devis{" +
            "id=" + getId() +
            ", prixTotal=" + getPrixTotal() +
            ", prixHT=" + getPrixHT() +
            ", prixService=" + getPrixService() +
            ", dureeProjet='" + getDureeProjet() + "'" +
            "}";
    }
}
