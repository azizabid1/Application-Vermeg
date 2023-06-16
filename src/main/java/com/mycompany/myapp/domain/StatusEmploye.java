package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A StatusEmploye.
 */
@Table("status_employe")
public class StatusEmploye implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("disponibilite")
    private Boolean disponibilite;

    @Column("mission")
    private Boolean mission;

    @Column("debut_conge")
    private LocalDate debutConge;

    @Column("fin_conge")
    private LocalDate finConge;

    @Transient
    private User userId;

    @Column("user_id_id")
    private Long userIdId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StatusEmploye id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDisponibilite() {
        return this.disponibilite;
    }

    public StatusEmploye disponibilite(Boolean disponibilite) {
        this.setDisponibilite(disponibilite);
        return this;
    }

    public void setDisponibilite(Boolean disponibilite) {
        this.disponibilite = disponibilite;
    }

    public Boolean getMission() {
        return this.mission;
    }

    public StatusEmploye mission(Boolean mission) {
        this.setMission(mission);
        return this;
    }

    public void setMission(Boolean mission) {
        this.mission = mission;
    }

    public LocalDate getDebutConge() {
        return this.debutConge;
    }

    public StatusEmploye debutConge(LocalDate debutConge) {
        this.setDebutConge(debutConge);
        return this;
    }

    public void setDebutConge(LocalDate debutConge) {
        this.debutConge = debutConge;
    }

    public LocalDate getFinConge() {
        return this.finConge;
    }

    public StatusEmploye finConge(LocalDate finConge) {
        this.setFinConge(finConge);
        return this;
    }

    public void setFinConge(LocalDate finConge) {
        this.finConge = finConge;
    }

    public User getUserId() {
        return this.userId;
    }

    public void setUserId(User user) {
        this.userId = user;
        this.userIdId = user != null ? user.getId() : null;
    }

    public StatusEmploye userId(User user) {
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
        if (!(o instanceof StatusEmploye)) {
            return false;
        }
        return id != null && id.equals(((StatusEmploye) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StatusEmploye{" +
            "id=" + getId() +
            ", disponibilite='" + getDisponibilite() + "'" +
            ", mission='" + getMission() + "'" +
            ", debutConge='" + getDebutConge() + "'" +
            ", finConge='" + getFinConge() + "'" +
            "}";
    }
}
