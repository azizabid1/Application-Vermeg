package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DevisTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Devis.class);
        Devis devis1 = new Devis();
        devis1.setId(1L);
        Devis devis2 = new Devis();
        devis2.setId(devis1.getId());
        assertThat(devis1).isEqualTo(devis2);
        devis2.setId(2L);
        assertThat(devis1).isNotEqualTo(devis2);
        devis1.setId(null);
        assertThat(devis1).isNotEqualTo(devis2);
    }
}
