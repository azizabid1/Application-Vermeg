package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.UUIDFilter;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Devis} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.DevisResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /devis?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class DevisCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private DoubleFilter prixTotal;

    private DoubleFilter prixHT;

    private DoubleFilter prixService;

    private FloatFilter dureeProjet;

    private UUIDFilter userUuid;

    private LongFilter projetId;

    private Boolean distinct;

    public DevisCriteria() {}

    public DevisCriteria(DevisCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.prixTotal = other.prixTotal == null ? null : other.prixTotal.copy();
        this.prixHT = other.prixHT == null ? null : other.prixHT.copy();
        this.prixService = other.prixService == null ? null : other.prixService.copy();
        this.dureeProjet = other.dureeProjet == null ? null : other.dureeProjet.copy();
        this.userUuid = other.userUuid == null ? null : other.userUuid.copy();
        this.projetId = other.projetId == null ? null : other.projetId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public DevisCriteria copy() {
        return new DevisCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public DoubleFilter getPrixTotal() {
        return prixTotal;
    }

    public DoubleFilter prixTotal() {
        if (prixTotal == null) {
            prixTotal = new DoubleFilter();
        }
        return prixTotal;
    }

    public void setPrixTotal(DoubleFilter prixTotal) {
        this.prixTotal = prixTotal;
    }

    public DoubleFilter getPrixHT() {
        return prixHT;
    }

    public DoubleFilter prixHT() {
        if (prixHT == null) {
            prixHT = new DoubleFilter();
        }
        return prixHT;
    }

    public void setPrixHT(DoubleFilter prixHT) {
        this.prixHT = prixHT;
    }

    public DoubleFilter getPrixService() {
        return prixService;
    }

    public DoubleFilter prixService() {
        if (prixService == null) {
            prixService = new DoubleFilter();
        }
        return prixService;
    }

    public void setPrixService(DoubleFilter prixService) {
        this.prixService = prixService;
    }

    public FloatFilter getDureeProjet() {
        return dureeProjet;
    }

    public FloatFilter dureeProjet() {
        if (dureeProjet == null) {
            dureeProjet = new FloatFilter();
        }
        return dureeProjet;
    }

    public void setDureeProjet(FloatFilter dureeProjet) {
        this.dureeProjet = dureeProjet;
    }

    public UUIDFilter getUserUuid() {
        return userUuid;
    }

    public UUIDFilter userUuid() {
        if (userUuid == null) {
            userUuid = new UUIDFilter();
        }
        return userUuid;
    }

    public void setUserUuid(UUIDFilter userUuid) {
        this.userUuid = userUuid;
    }

    public LongFilter getProjetId() {
        return projetId;
    }

    public LongFilter projetId() {
        if (projetId == null) {
            projetId = new LongFilter();
        }
        return projetId;
    }

    public void setProjetId(LongFilter projetId) {
        this.projetId = projetId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DevisCriteria that = (DevisCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(prixTotal, that.prixTotal) &&
            Objects.equals(prixHT, that.prixHT) &&
            Objects.equals(prixService, that.prixService) &&
            Objects.equals(dureeProjet, that.dureeProjet) &&
            Objects.equals(userUuid, that.userUuid) &&
            Objects.equals(projetId, that.projetId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, prixTotal, prixHT, prixService, dureeProjet, userUuid, projetId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DevisCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (prixTotal != null ? "prixTotal=" + prixTotal + ", " : "") +
            (prixHT != null ? "prixHT=" + prixHT + ", " : "") +
            (prixService != null ? "prixService=" + prixService + ", " : "") +
            (dureeProjet != null ? "dureeProjet=" + dureeProjet + ", " : "") +
            (userUuid != null ? "userUuid=" + userUuid + ", " : "") +
            (projetId != null ? "projetId=" + projetId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
