package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.TypeDepartement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Departement} entity.
 */
public class DepartementDTO implements Serializable {

    private Long id;

    private TypeDepartement nom;

    private Set<UserDTO> users = new HashSet<>();

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

    public Set<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(Set<UserDTO> users) {
        this.users = users;
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
            ", users=" + getUsers() +
            "}";
    }
}
