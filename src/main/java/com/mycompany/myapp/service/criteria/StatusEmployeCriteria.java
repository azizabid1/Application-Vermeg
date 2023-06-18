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
import tech.jhipster.service.filter.UUIDFilter;

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

    private UUIDFilter userUuid;

    private BooleanFilter disponibilite;

    private BooleanFilter mission;

    private LocalDateFilter debutConge;

    private LocalDateFilter finConge;

    private LongFilter userIdId;

    private Boolean distinct;

    public StatusEmployeCriteria() {}

    public StatusEmployeCriteria(StatusEmployeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.userUuid = other.userUuid == null ? null : other.userUuid.copy();
        this.disponibilite = other.disponibilite == null ? null : other.disponibilite.copy();
        this.mission = other.mission == null ? null : other.mission.copy();
        this.debutConge = other.debutConge == null ? null : other.debutConge.copy();
        this.finConge = other.finConge == null ? null : other.finConge.copy();
        this.userIdId = other.userIdId == null ? null : other.userIdId.copy();
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

    public LongFilter getUserIdId() {
        return userIdId;
    }

    public LongFilter userIdId() {
        if (userIdId == null) {
            userIdId = new LongFilter();
        }
        return userIdId;
    }

    public void setUserIdId(LongFilter userIdId) {
        this.userIdId = userIdId;
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
            Objects.equals(userUuid, that.userUuid) &&
            Objects.equals(disponibilite, that.disponibilite) &&
            Objects.equals(mission, that.mission) &&
            Objects.equals(debutConge, that.debutConge) &&
            Objects.equals(finConge, that.finConge) &&
            Objects.equals(userIdId, that.userIdId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userUuid, disponibilite, mission, debutConge, finConge, userIdId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StatusEmployeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (userUuid != null ? "userUuid=" + userUuid + ", " : "") +
            (disponibilite != null ? "disponibilite=" + disponibilite + ", " : "") +
            (mission != null ? "mission=" + mission + ", " : "") +
            (debutConge != null ? "debutConge=" + debutConge + ", " : "") +
            (finConge != null ? "finConge=" + finConge + ", " : "") +
            (userIdId != null ? "userIdId=" + userIdId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}