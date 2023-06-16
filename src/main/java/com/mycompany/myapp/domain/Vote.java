package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.Rendement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * The Employee entity.
 */
@Table("vote")
public class Vote implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("type_vote")
    private Rendement typeVote;

    @Transient
    @JsonIgnoreProperties(value = { "userId", "votes" }, allowSetters = true)
    private Set<Equipe> equipes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Vote id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rendement getTypeVote() {
        return this.typeVote;
    }

    public Vote typeVote(Rendement typeVote) {
        this.setTypeVote(typeVote);
        return this;
    }

    public void setTypeVote(Rendement typeVote) {
        this.typeVote = typeVote;
    }

    public Set<Equipe> getEquipes() {
        return this.equipes;
    }

    public void setEquipes(Set<Equipe> equipes) {
        if (this.equipes != null) {
            this.equipes.forEach(i -> i.removeVote(this));
        }
        if (equipes != null) {
            equipes.forEach(i -> i.addVote(this));
        }
        this.equipes = equipes;
    }

    public Vote equipes(Set<Equipe> equipes) {
        this.setEquipes(equipes);
        return this;
    }

    public Vote addEquipe(Equipe equipe) {
        this.equipes.add(equipe);
        equipe.getVotes().add(this);
        return this;
    }

    public Vote removeEquipe(Equipe equipe) {
        this.equipes.remove(equipe);
        equipe.getVotes().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vote)) {
            return false;
        }
        return id != null && id.equals(((Vote) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Vote{" +
            "id=" + getId() +
            ", typeVote='" + getTypeVote() + "'" +
            "}";
    }
}
