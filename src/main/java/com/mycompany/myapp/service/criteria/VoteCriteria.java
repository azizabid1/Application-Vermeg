package com.mycompany.myapp.service.criteria;

import com.mycompany.myapp.domain.enumeration.Rendement;
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

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Vote} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.VoteResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /votes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class VoteCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Rendement
     */
    public static class RendementFilter extends Filter<Rendement> {

        public RendementFilter() {}

        public RendementFilter(RendementFilter filter) {
            super(filter);
        }

        @Override
        public RendementFilter copy() {
            return new RendementFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private RendementFilter typeVote;

    private LongFilter equipesId;

    private Boolean distinct;

    public VoteCriteria() {}

    public VoteCriteria(VoteCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.typeVote = other.typeVote == null ? null : other.typeVote.copy();
        this.equipesId = other.equipesId == null ? null : other.equipesId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public VoteCriteria copy() {
        return new VoteCriteria(this);
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

    public RendementFilter getTypeVote() {
        return typeVote;
    }

    public RendementFilter typeVote() {
        if (typeVote == null) {
            typeVote = new RendementFilter();
        }
        return typeVote;
    }

    public void setTypeVote(RendementFilter typeVote) {
        this.typeVote = typeVote;
    }

    public LongFilter getEquipesId() {
        return equipesId;
    }

    public LongFilter equipesId() {
        if (equipesId == null) {
            equipesId = new LongFilter();
        }
        return equipesId;
    }

    public void setEquipesId(LongFilter equipesId) {
        this.equipesId = equipesId;
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
        final VoteCriteria that = (VoteCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(typeVote, that.typeVote) &&
            Objects.equals(equipesId, that.equipesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, typeVote, equipesId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VoteCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (typeVote != null ? "typeVote=" + typeVote + ", " : "") +
            (equipesId != null ? "equipesId=" + equipesId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
