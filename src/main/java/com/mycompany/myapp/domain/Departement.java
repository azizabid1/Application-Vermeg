package com.mycompany.myapp.domain;

import com.mycompany.myapp.domain.enumeration.TypeDepartement;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

/**
 * A Departement.
 */
@Entity
@Table(name = "departement")
public class Departement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Min(value = 4L)
    @Max(value = 6L)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "nom", nullable = false, unique = true)
    private TypeDepartement nom;

    @NotNull
    @Type(type = "uuid-char")
    @Column(name = "user_uuid", length = 36, nullable = false)
    private UUID userUuid;

    @ManyToOne
    private User userId;

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

    public User getUserId() {
        return this.userId;
    }

    public void setUserId(User user) {
        this.userId = user;
    }

    public Departement userId(User user) {
        this.setUserId(user);
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
