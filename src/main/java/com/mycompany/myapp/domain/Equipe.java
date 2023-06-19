package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.GenericGenerator;
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
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(generator = "uuid2")
    @Column(name = "user_uuid", length = 36, nullable = false)
    private UUID userUuid;

    @Min(value = 4L)
    @Max(value = 6L)
    @ManyToMany
    @JoinTable(
        name = "rel_equipe__users",
        joinColumns = @JoinColumn(name = "equipe_id"),
        inverseJoinColumns = @JoinColumn(name = "users_id")
    )
    private Set<User> users = new HashSet<>();

    @JsonIgnoreProperties(value = { "equipe", "taches", "devis" }, allowSetters = true)
    @OneToOne(mappedBy = "equipe")
    private Projet projet;

    @ManyToOne
    @JsonIgnoreProperties(value = { "equipes" }, allowSetters = true)
    private Vote vote;

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

    public Set<User> getUsers() {
        return this.users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Equipe users(Set<User> users) {
        this.setUsers(users);
        return this;
    }

    public Equipe addUsers(User user) {
        this.users.add(user);
        return this;
    }

    public Equipe removeUsers(User user) {
        this.users.remove(user);
        return this;
    }

    public Projet getProjet() {
        return this.projet;
    }

    public void setProjet(Projet projet) {
        if (this.projet != null) {
            this.projet.setEquipe(null);
        }
        if (projet != null) {
            projet.setEquipe(this);
        }
        this.projet = projet;
    }

    public Equipe projet(Projet projet) {
        this.setProjet(projet);
        return this;
    }

    public Vote getVote() {
        return this.vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }

    public Equipe vote(Vote vote) {
        this.setVote(vote);
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
