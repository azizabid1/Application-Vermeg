package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

/**
 * not an ignored comment
 */
@Entity
@Table(name = "devis")
public class Devis implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "prix_total")
    private Double prixTotal;

    @Column(name = "prix_ht")
    private Double prixHT;

    @Column(name = "prix_service")
    private Double prixService;

    @DecimalMin(value = "0")
    @Column(name = "duree_projet")
    private Float dureeProjet;

    @NotNull
    @Type(type = "uuid-char")
    @Column(name = "user_uuid", length = 36, nullable = false)
    private UUID userUuid;

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

    public Float getDureeProjet() {
        return this.dureeProjet;
    }

    public Devis dureeProjet(Float dureeProjet) {
        this.setDureeProjet(dureeProjet);
        return this;
    }

    public void setDureeProjet(Float dureeProjet) {
        this.dureeProjet = dureeProjet;
    }

    public UUID getUserUuid() {
        return this.userUuid;
    }

    public Devis userUuid(UUID userUuid) {
        this.setUserUuid(userUuid);
        return this;
    }

    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
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
            ", dureeProjet=" + getDureeProjet() +
            ", userUuid='" + getUserUuid() + "'" +
            "}";
    }
}
