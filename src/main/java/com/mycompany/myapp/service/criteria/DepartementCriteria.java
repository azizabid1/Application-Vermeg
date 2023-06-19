package com.mycompany.myapp.service.criteria;

import com.mycompany.myapp.domain.enumeration.TypeDepartement;
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
 * Criteria class for the {@link com.mycompany.myapp.domain.Departement} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.DepartementResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /departements?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class DepartementCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TypeDepartement
     */
    public static class TypeDepartementFilter extends Filter<TypeDepartement> {

        public TypeDepartementFilter() {}

        public TypeDepartementFilter(TypeDepartementFilter filter) {
            super(filter);
        }

        @Override
        public TypeDepartementFilter copy() {
            return new TypeDepartementFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private TypeDepartementFilter nom;

    private UUIDFilter userUuid;

    private LongFilter usersId;

    private Boolean distinct;

    public DepartementCriteria() {}

    public DepartementCriteria(DepartementCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nom = other.nom == null ? null : other.nom.copy();
        this.userUuid = other.userUuid == null ? null : other.userUuid.copy();
        this.usersId = other.usersId == null ? null : other.usersId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public DepartementCriteria copy() {
        return new DepartementCriteria(this);
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

    public TypeDepartementFilter getNom() {
        return nom;
    }

    public TypeDepartementFilter nom() {
        if (nom == null) {
            nom = new TypeDepartementFilter();
        }
        return nom;
    }

    public void setNom(TypeDepartementFilter nom) {
        this.nom = nom;
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
        final DepartementCriteria that = (DepartementCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nom, that.nom) &&
            Objects.equals(userUuid, that.userUuid) &&
            Objects.equals(usersId, that.usersId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, userUuid, usersId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DepartementCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nom != null ? "nom=" + nom + ", " : "") +
            (userUuid != null ? "userUuid=" + userUuid + ", " : "") +
            (usersId != null ? "usersId=" + usersId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
