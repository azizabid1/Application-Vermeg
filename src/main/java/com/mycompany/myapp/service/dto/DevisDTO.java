package com.mycompany.myapp.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Devis} entity.
 */
@Schema(description = "not an ignored comment")
public class DevisDTO implements Serializable {

    private Long id;

    private Double prixTotal;

    private Double prixHT;

    private Double prixService;

    private LocalDate dureeProjet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(Double prixTotal) {
        this.prixTotal = prixTotal;
    }

    public Double getPrixHT() {
        return prixHT;
    }

    public void setPrixHT(Double prixHT) {
        this.prixHT = prixHT;
    }

    public Double getPrixService() {
        return prixService;
    }

    public void setPrixService(Double prixService) {
        this.prixService = prixService;
    }

    public LocalDate getDureeProjet() {
        return dureeProjet;
    }

    public void setDureeProjet(LocalDate dureeProjet) {
        this.dureeProjet = dureeProjet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DevisDTO)) {
            return false;
        }

        DevisDTO devisDTO = (DevisDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, devisDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DevisDTO{" +
            "id=" + getId() +
            ", prixTotal=" + getPrixTotal() +
            ", prixHT=" + getPrixHT() +
            ", prixService=" + getPrixService() +
            ", dureeProjet='" + getDureeProjet() + "'" +
            "}";
    }
}
