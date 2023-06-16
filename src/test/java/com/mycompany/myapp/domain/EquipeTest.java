package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EquipeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Equipe.class);
        Equipe equipe1 = new Equipe();
        equipe1.setId(1L);
        Equipe equipe2 = new Equipe();
        equipe2.setId(equipe1.getId());
        assertThat(equipe1).isEqualTo(equipe2);
        equipe2.setId(2L);
        assertThat(equipe1).isNotEqualTo(equipe2);
        equipe1.setId(null);
        assertThat(equipe1).isNotEqualTo(equipe2);
    }
}
