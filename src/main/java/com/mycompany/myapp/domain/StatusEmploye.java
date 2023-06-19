package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 * A StatusEmploye.
 */
@Entity
@Table(name = "status_employe")
public class StatusEmploye implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(generator = "uuid2")
    @Column(name = "user_uuid", length = 36, nullable = false)
    private UUID userUuid;

    @Column(name = "disponibilite")
    private Boolean disponibilite;

    @Column(name = "mission")
    private Boolean mission;

    @Column(name = "debut_conge")
    private LocalDate debutConge;

    @Column(name = "fin_conge")
    private LocalDate finConge;

    @ManyToMany
    @JoinTable(
        name = "rel_status_employe__users",
        joinColumns = @JoinColumn(name = "status_employe_id"),
        inverseJoinColumns = @JoinColumn(name = "users_id")
    )
    private Set<User> users = new HashSet<>();

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

    public UUID getUserUuid() {
        return this.userUuid;
    }

    public StatusEmploye userUuid(UUID userUuid) {
        this.setUserUuid(userUuid);
        return this;
    }

    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
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

    public Set<User> getUsers() {
        return this.users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public StatusEmploye users(Set<User> users) {
        this.setUsers(users);
        return this;
    }

    public StatusEmploye addUsers(User user) {
        this.users.add(user);
        return this;
    }

    public StatusEmploye removeUsers(User user) {
        this.users.remove(user);
        return this;
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
            ", userUuid='" + getUserUuid() + "'" +
            ", disponibilite='" + getDisponibilite() + "'" +
            ", mission='" + getMission() + "'" +
            ", debutConge='" + getDebutConge() + "'" +
            ", finConge='" + getFinConge() + "'" +
            "}";
    }
}
