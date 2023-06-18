package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.StatusEmploye} entity.
 */
public class StatusEmployeDTO implements Serializable {

    private Long id;

    @NotNull
    private UUID userUuid;

    private Boolean disponibilite;

    private Boolean mission;

    private LocalDate debutConge;

    private LocalDate finConge;

    private UserDTO userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
    }

    public Boolean getDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(Boolean disponibilite) {
        this.disponibilite = disponibilite;
    }

    public Boolean getMission() {
        return mission;
    }

    public void setMission(Boolean mission) {
        this.mission = mission;
    }

    public LocalDate getDebutConge() {
        return debutConge;
    }

    public void setDebutConge(LocalDate debutConge) {
        this.debutConge = debutConge;
    }

    public LocalDate getFinConge() {
        return finConge;
    }

    public void setFinConge(LocalDate finConge) {
        this.finConge = finConge;
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
        if (!(o instanceof StatusEmployeDTO)) {
            return false;
        }

        StatusEmployeDTO statusEmployeDTO = (StatusEmployeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, statusEmployeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StatusEmployeDTO{" +
            "id=" + getId() +
            ", userUuid='" + getUserUuid() + "'" +
            ", disponibilite='" + getDisponibilite() + "'" +
            ", mission='" + getMission() + "'" +
            ", debutConge='" + getDebutConge() + "'" +
            ", finConge='" + getFinConge() + "'" +
            ", userId=" + getUserId() +
            "}";
    }
}
