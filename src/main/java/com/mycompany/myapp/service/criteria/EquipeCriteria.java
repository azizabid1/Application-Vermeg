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
 * Criteria class for the {@link com.mycompany.myapp.domain.Equipe} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.EquipeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /equipes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class EquipeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nom;

    private LongFilter nombrePersonne;

    private UUIDFilter userUuid;

    private LongFilter userIdId;

    private LongFilter voteId;

    private Boolean distinct;

    public EquipeCriteria() {}

    public EquipeCriteria(EquipeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nom = other.nom == null ? null : other.nom.copy();
        this.nombrePersonne = other.nombrePersonne == null ? null : other.nombrePersonne.copy();
        this.userUuid = other.userUuid == null ? null : other.userUuid.copy();
        this.userIdId = other.userIdId == null ? null : other.userIdId.copy();
        this.voteId = other.voteId == null ? null : other.voteId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public EquipeCriteria copy() {
        return new EquipeCriteria(this);
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

    public StringFilter getNom() {
        return nom;
    }

    public StringFilter nom() {
        if (nom == null) {
            nom = new StringFilter();
        }
        return nom;
    }

    public void setNom(StringFilter nom) {
        this.nom = nom;
    }

    public LongFilter getNombrePersonne() {
        return nombrePersonne;
    }

    public LongFilter nombrePersonne() {
        if (nombrePersonne == null) {
            nombrePersonne = new LongFilter();
        }
        return nombrePersonne;
    }

    public void setNombrePersonne(LongFilter nombrePersonne) {
        this.nombrePersonne = nombrePersonne;
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

    public LongFilter getVoteId() {
        return voteId;
    }

    public LongFilter voteId() {
        if (voteId == null) {
            voteId = new LongFilter();
        }
        return voteId;
    }

    public void setVoteId(LongFilter voteId) {
        this.voteId = voteId;
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
        final EquipeCriteria that = (EquipeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nom, that.nom) &&
            Objects.equals(nombrePersonne, that.nombrePersonne) &&
            Objects.equals(userUuid, that.userUuid) &&
            Objects.equals(userIdId, that.userIdId) &&
            Objects.equals(voteId, that.voteId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, nombrePersonne, userUuid, userIdId, voteId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EquipeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nom != null ? "nom=" + nom + ", " : "") +
            (nombrePersonne != null ? "nombrePersonne=" + nombrePersonne + ", " : "") +
            (userUuid != null ? "userUuid=" + userUuid + ", " : "") +
            (userIdId != null ? "userIdId=" + userIdId + ", " : "") +
            (voteId != null ? "voteId=" + voteId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
