package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.TypeDepartement;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Departement} entity.
 */
public class DepartementDTO implements Serializable {

    private Long id;

    private TypeDepartement nom;

    private UserDTO userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeDepartement getNom() {
        return nom;
    }

    public void setNom(TypeDepartement nom) {
        this.nom = nom;
    }

    public UserDTO getUserId() {
        return userId;
    }

    public void setUserId(UserDTO userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DepartementDTO)) {
            return false;
        }

        DepartementDTO departementDTO = (DepartementDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, departementDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DepartementDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", userId=" + getUserId() +
            "}";
    }
}
