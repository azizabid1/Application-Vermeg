package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Equipe} entity.
 */
public class EquipeDTO implements Serializable {

    private Long id;

    private String nom;

    private Long nombrePersonne;

    private UserDTO userId;

    private Set<VoteDTO> votes = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Long getNombrePersonne() {
        return nombrePersonne;
    }

    public void setNombrePersonne(Long nombrePersonne) {
        this.nombrePersonne = nombrePersonne;
    }

    public UserDTO getUserId() {
        return userId;
    }

    public void setUserId(UserDTO userId) {
        this.userId = userId;
    }

    public Set<VoteDTO> getVotes() {
        return votes;
    }

    public void setVotes(Set<VoteDTO> votes) {
        this.votes = votes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EquipeDTO)) {
            return false;
        }

        EquipeDTO equipeDTO = (EquipeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, equipeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EquipeDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", nombrePersonne=" + getNombrePersonne() +
            ", userId=" + getUserId() +
            ", votes=" + getVotes() +
            "}";
    }
}
