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
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.UUIDFilter;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Tache} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.TacheResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /taches?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class TacheCriteria implements Serializable, Criteria {

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

    private StringFilter titre;

    private StringFilter description;

    private StatusFilter statusTache;

    private LongFilter projetId;

    private Boolean distinct;

    public TacheCriteria() {}

    public TacheCriteria(TacheCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.userUuid = other.userUuid == null ? null : other.userUuid.copy();
        this.titre = other.titre == null ? null : other.titre.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.statusTache = other.statusTache == null ? null : other.statusTache.copy();
        this.projetId = other.projetId == null ? null : other.projetId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TacheCriteria copy() {
        return new TacheCriteria(this);
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

    public StringFilter getTitre() {
        return titre;
    }

    public StringFilter titre() {
        if (titre == null) {
            titre = new StringFilter();
        }
        return titre;
    }

    public void setTitre(StringFilter titre) {
        this.titre = titre;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StatusFilter getStatusTache() {
        return statusTache;
    }

    public StatusFilter statusTache() {
        if (statusTache == null) {
            statusTache = new StatusFilter();
        }
        return statusTache;
    }

    public void setStatusTache(StatusFilter statusTache) {
        this.statusTache = statusTache;
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
        final TacheCriteria that = (TacheCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(userUuid, that.userUuid) &&
            Objects.equals(titre, that.titre) &&
            Objects.equals(description, that.description) &&
            Objects.equals(statusTache, that.statusTache) &&
            Objects.equals(projetId, that.projetId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userUuid, titre, description, statusTache, projetId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TacheCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (userUuid != null ? "userUuid=" + userUuid + ", " : "") +
            (titre != null ? "titre=" + titre + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (statusTache != null ? "statusTache=" + statusTache + ", " : "") +
            (projetId != null ? "projetId=" + projetId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
