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
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.StatusEmploye} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.StatusEmployeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /status-employes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class StatusEmployeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BooleanFilter disponibilite;

    private BooleanFilter mission;

    private LocalDateFilter debutConge;

    private LocalDateFilter finConge;

    private LongFilter usersId;

    private Boolean distinct;

    public StatusEmployeCriteria() {}

    public StatusEmployeCriteria(StatusEmployeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.disponibilite = other.disponibilite == null ? null : other.disponibilite.copy();
        this.mission = other.mission == null ? null : other.mission.copy();
        this.debutConge = other.debutConge == null ? null : other.debutConge.copy();
        this.finConge = other.finConge == null ? null : other.finConge.copy();
        this.usersId = other.usersId == null ? null : other.usersId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public StatusEmployeCriteria copy() {
        return new StatusEmployeCriteria(this);
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

    public BooleanFilter getDisponibilite() {
        return disponibilite;
    }

    public BooleanFilter disponibilite() {
        if (disponibilite == null) {
            disponibilite = new BooleanFilter();
        }
        return disponibilite;
    }

    public void setDisponibilite(BooleanFilter disponibilite) {
        this.disponibilite = disponibilite;
    }

    public BooleanFilter getMission() {
        return mission;
    }

    public BooleanFilter mission() {
        if (mission == null) {
            mission = new BooleanFilter();
        }
        return mission;
    }

    public void setMission(BooleanFilter mission) {
        this.mission = mission;
    }

    public LocalDateFilter getDebutConge() {
        return debutConge;
    }

    public LocalDateFilter debutConge() {
        if (debutConge == null) {
            debutConge = new LocalDateFilter();
        }
        return debutConge;
    }

    public void setDebutConge(LocalDateFilter debutConge) {
        this.debutConge = debutConge;
    }

    public LocalDateFilter getFinConge() {
        return finConge;
    }

    public LocalDateFilter finConge() {
        if (finConge == null) {
            finConge = new LocalDateFilter();
        }
        return finConge;
    }

    public void setFinConge(LocalDateFilter finConge) {
        this.finConge = finConge;
    }

    public LongFilter getUsersId() {
        return usersId;
    }

    public LongFilter usersId() {
        if (usersId == null) {
            usersId = new LongFilter();
        }
        return usersId;
    }

    public void setUsersId(LongFilter usersId) {
        this.usersId = usersId;
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
        final StatusEmployeCriteria that = (StatusEmployeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(disponibilite, that.disponibilite) &&
            Objects.equals(mission, that.mission) &&
            Objects.equals(debutConge, that.debutConge) &&
            Objects.equals(finConge, that.finConge) &&
            Objects.equals(usersId, that.usersId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, disponibilite, mission, debutConge, finConge, usersId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StatusEmployeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (disponibilite != null ? "disponibilite=" + disponibilite + ", " : "") +
            (mission != null ? "mission=" + mission + ", " : "") +
            (debutConge != null ? "debutConge=" + debutConge + ", " : "") +
            (finConge != null ? "finConge=" + finConge + ", " : "") +
            (usersId != null ? "usersId=" + usersId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
