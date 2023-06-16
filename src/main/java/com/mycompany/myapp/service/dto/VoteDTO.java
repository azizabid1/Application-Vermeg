package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.Rendement;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Vote} entity.
 */
@Schema(description = "The Employee entity.")
public class VoteDTO implements Serializable {

    private Long id;

    private Rendement typeVote;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rendement getTypeVote() {
        return typeVote;
    }

    public void setTypeVote(Rendement typeVote) {
        this.typeVote = typeVote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VoteDTO)) {
            return false;
        }

        VoteDTO voteDTO = (VoteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, voteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VoteDTO{" +
            "id=" + getId() +
            ", typeVote='" + getTypeVote() + "'" +
            "}";
    }
}
