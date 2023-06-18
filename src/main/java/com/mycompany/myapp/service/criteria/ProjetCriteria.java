package com.mycompany.myapp.service.criteria;

import com.mycompany.myapp.domain.enumeration.Status;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.UUIDFilter;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Projet} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.ProjetResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /projets?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class ProjetCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Status
     */
    public static class StatusFilter extends Filter<Status> {

        public StatusFilter() {}

        public StatusFilter(StatusFilter filter) {
            super(filter);
        }

        @Override
        public StatusFilter copy() {
            return new StatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private UUIDFilter userUuid;

    private StringFilter nomProjet;

    private LocalDateFilter dateDebut;

    private LocalDateFilter dateFin;

    private StringFilter technologies;

    private StatusFilter statusProjet;

    private LongFilter nombreTotal;

    private LongFilter nombreRestant;

    private LongFilter devisId;

    private LongFilter equipeId;

    private LongFilter tacheId;

    private Boolean distinct;

    public ProjetCriteria() {}

    public ProjetCriteria(ProjetCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.userUuid = other.userUuid == null ? null : other.userUuid.copy();
        this.nomProjet = other.nomProjet == null ? null : other.nomProjet.copy();
        this.dateDebut = other.dateDebut == null ? null : other.dateDebut.copy();
        this.dateFin = other.dateFin == null ? null : other.dateFin.copy();
        this.technologies = other.technologies == null ? null : other.technologies.copy();
        this.statusProjet = other.statusProjet == null ? null : other.statusProjet.copy();
        this.nombreTotal = other.nombreTotal == null ? null : other.nombreTotal.copy();
        this.nombreRestant = other.nombreRestant == null ? null : other.nombreRestant.copy();
        this.devisId = other.devisId == null ? null : other.devisId.copy();
        this.equipeId = other.equipeId == null ? null : other.equipeId.copy();
        this.tacheId = other.tacheId == null ? null : other.tacheId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ProjetCriteria copy() {
        return new ProjetCriteria(this);
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

    public StringFilter getNomProjet() {
        return nomProjet;
    }

    public StringFilter nomProjet() {
        if (nomProjet == null) {
            nomProjet = new StringFilter();
        }
        return nomProjet;
    }

    public void setNomProjet(StringFilter nomProjet) {
        this.nomProjet = nomProjet;
    }

    public LocalDateFilter getDateDebut() {
        return dateDebut;
    }

    public LocalDateFilter dateDebut() {
        if (dateDebut == null) {
            dateDebut = new LocalDateFilter();
        }
        return dateDebut;
    }

    public void setDateDebut(LocalDateFilter dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateFilter getDateFin() {
        return dateFin;
    }

    public LocalDateFilter dateFin() {
        if (dateFin == null) {
            dateFin = new LocalDateFilter();
        }
        return dateFin;
    }

    public void setDateFin(LocalDateFilter dateFin) {
        this.dateFin = dateFin;
    }

    public StringFilter getTechnologies() {
        return technologies;
    }

    public StringFilter technologies() {
        if (technologies == null) {
            technologies = new StringFilter();
        }
        return technologies;
    }

    public void setTechnologies(StringFilter technologies) {
        this.technologies = technologies;
    }

    public StatusFilter getStatusProjet() {
        return statusProjet;
    }

    public StatusFilter statusProjet() {
        if (statusProjet == null) {
            statusProjet = new StatusFilter();
        }
        return statusProjet;
    }

    public void setStatusProjet(StatusFilter statusProjet) {
        this.statusProjet = statusProjet;
    }

    public LongFilter getNombreTotal() {
        return nombreTotal;
    }

    public LongFilter nombreTotal() {
        if (nombreTotal == null) {
            nombreTotal = new LongFilter();
        }
        return nombreTotal;
    }

    public void setNombreTotal(LongFilter nombreTotal) {
        this.nombreTotal = nombreTotal;
    }

    public LongFilter getNombreRestant() {
        return nombreRestant;
    }

    public LongFilter nombreRestant() {
        if (nombreRestant == null) {
            nombreRestant = new LongFilter();
        }
        return nombreRestant;
    }

    public void setNombreRestant(LongFilter nombreRestant) {
        this.nombreRestant = nombreRestant;
    }

    public LongFilter getDevisId() {
        return devisId;
    }

    public LongFilter devisId() {
        if (devisId == null) {
            devisId = new LongFilter();
        }
        return devisId;
    }

    public void setDevisId(LongFilter devisId) {
        this.devisId = devisId;
    }

    public LongFilter getEquipeId() {
        return equipeId;
    }

    public LongFilter equipeId() {
        if (equipeId == null) {
            equipeId = new LongFilter();
        }
        return equipeId;
    }

    public void setEquipeId(LongFilter equipeId) {
        this.equipeId = equipeId;
    }

    public LongFilter getTacheId() {
        return tacheId;
    }

    public LongFilter tacheId() {
        if (tacheId == null) {
            tacheId = new LongFilter();
        }
        return tacheId;
    }

    public void setTacheId(LongFilter tacheId) {
        this.tacheId = tacheId;
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
        final ProjetCriteria that = (ProjetCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(userUuid, that.userUuid) &&
            Objects.equals(nomProjet, that.nomProjet) &&
            Objects.equals(dateDebut, that.dateDebut) &&
            Objects.equals(dateFin, that.dateFin) &&
            Objects.equals(technologies, that.technologies) &&
            Objects.equals(statusProjet, that.statusProjet) &&
            Objects.equals(nombreTotal, that.nombreTotal) &&
            Objects.equals(nombreRestant, that.nombreRestant) &&
            Objects.equals(devisId, that.devisId) &&
            Objects.equals(equipeId, that.equipeId) &&
            Objects.equals(tacheId, that.tacheId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            userUuid,
            nomProjet,
            dateDebut,
            dateFin,
            technologies,
            statusProjet,
            nombreTotal,
            nombreRestant,
            devisId,
            equipeId,
            tacheId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjetCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (userUuid != null ? "userUuid=" + userUuid + ", " : "") +
            (nomProjet != null ? "nomProjet=" + nomProjet + ", " : "") +
            (dateDebut != null ? "dateDebut=" + dateDebut + ", " : "") +
            (dateFin != null ? "dateFin=" + dateFin + ", " : "") +
            (technologies != null ? "technologies=" + technologies + ", " : "") +
            (statusProjet != null ? "statusProjet=" + statusProjet + ", " : "") +
            (nombreTotal != null ? "nombreTotal=" + nombreTotal + ", " : "") +
            (nombreRestant != null ? "nombreRestant=" + nombreRestant + ", " : "") +
            (devisId != null ? "devisId=" + devisId + ", " : "") +
            (equipeId != null ? "equipeId=" + equipeId + ", " : "") +
            (tacheId != null ? "tacheId=" + tacheId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
