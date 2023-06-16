package com.mycompany.myapp.domain;

import com.mycompany.myapp.domain.enumeration.TypeDepartement;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Departement.
 */
@Table("departement")
public class Departement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nom")
    private TypeDepartement nom;

    @Transient
    private User userId;

    @Column("user_id_id")
    private Long userIdId;

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

    public User getUserId() {
        return this.userId;
    }

    public void setUserId(User user) {
        this.userId = user;
        this.userIdId = user != null ? user.getId() : null;
    }

    public Departement userId(User user) {
        this.setUserId(user);
        return this;
    }

    public Long getUserIdId() {
        return this.userIdId;
    }

    public void setUserIdId(Long user) {
        this.userIdId = user;
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
            "}";
    }
}
