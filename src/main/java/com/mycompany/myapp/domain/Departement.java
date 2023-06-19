package com.mycompany.myapp.domain;

import com.mycompany.myapp.domain.enumeration.TypeDepartement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 * A Departement.
 */
@Entity
@Table(name = "departement")
public class Departement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "nom", unique = true)
    private TypeDepartement nom;

    @NotNull
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(generator = "uuid2")
    @Column(name = "user_uuid", length = 36, nullable = false)
    private UUID userUuid;

    @ManyToMany
    @JoinTable(
        name = "rel_departement__users",
        joinColumns = @JoinColumn(name = "departement_id"),
        inverseJoinColumns = @JoinColumn(name = "users_id")
    )
    private Set<User> users = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Departement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeDepartement getNom() {
        return this.nom;
    }

    public Departement nom(TypeDepartement nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(TypeDepartement nom) {
        this.nom = nom;
    }

    public UUID getUserUuid() {
        return this.userUuid;
    }

    public Departement userUuid(UUID userUuid) {
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

    public Departement users(Set<User> users) {
        this.setUsers(users);
        return this;
    }

    public Departement addUsers(User user) {
        this.users.add(user);
        return this;
    }

    public Departement removeUsers(User user) {
        this.users.remove(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Departement)) {
            return false;
        }
        return id != null && id.equals(((Departement) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Departement{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", userUuid='" + getUserUuid() + "'" +
            "}";
    }
}
