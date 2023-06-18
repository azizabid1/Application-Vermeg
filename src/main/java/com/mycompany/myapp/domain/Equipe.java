package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

/**
 * A Equipe.
 */
@Entity
@Table(name = "equipe")
public class Equipe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Min(value = 4L)
    @Max(value = 6L)
    @Column(name = "nombre_personne")
    private Long nombrePersonne;

    @NotNull
    @Type(type = "uuid-char")
    @Column(name = "user_uuid", length = 36, nullable = false)
    private UUID userUuid;

    @ManyToOne
    private User userId;

    @ManyToMany
    @JoinTable(name = "rel_equipe__vote", joinColumns = @JoinColumn(name = "equipe_id"), inverseJoinColumns = @JoinColumn(name = "vote_id"))
    @JsonIgnoreProperties(value = { "equipes" }, allowSetters = true)
    private Set<Vote> votes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Equipe id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Equipe nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Long getNombrePersonne() {
        return this.nombrePersonne;
    }

    public Equipe nombrePersonne(Long nombrePersonne) {
        this.setNombrePersonne(nombrePersonne);
        return this;
    }

    public void setNombrePersonne(Long nombrePersonne) {
        this.nombrePersonne = nombrePersonne;
    }

    public UUID getUserUuid() {
        return this.userUuid;
    }

    public Equipe userUuid(UUID userUuid) {
        this.setUserUuid(userUuid);
        return this;
    }

    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
    }

    public User getUserId() {
        return this.userId;
    }

    public void setUserId(User user) {
        this.userId = user;
    }

    public Equipe userId(User user) {
        this.setUserId(user);
        return this;
    }

    public Set<Vote> getVotes() {
        return this.votes;
    }

    public void setVotes(Set<Vote> votes) {
        this.votes = votes;
    }

    public Equipe votes(Set<Vote> votes) {
        this.setVotes(votes);
        return this;
    }

    public Equipe addVote(Vote vote) {
        this.votes.add(vote);
        vote.getEquipes().add(this);
        return this;
    }

    public Equipe removeVote(Vote vote) {
        this.votes.remove(vote);
        vote.getEquipes().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Equipe)) {
            return false;
        }
        return id != null && id.equals(((Equipe) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Equipe{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", nombrePersonne=" + getNombrePersonne() +
            ", userUuid='" + getUserUuid() + "'" +
            "}";
    }
}
