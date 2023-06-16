package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DevisDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DevisDTO.class);
        DevisDTO devisDTO1 = new DevisDTO();
        devisDTO1.setId(1L);
        DevisDTO devisDTO2 = new DevisDTO();
        assertThat(devisDTO1).isNotEqualTo(devisDTO2);
        devisDTO2.setId(devisDTO1.getId());
        assertThat(devisDTO1).isEqualTo(devisDTO2);
        devisDTO2.setId(2L);
        assertThat(devisDTO1).isNotEqualTo(devisDTO2);
        devisDTO1.setId(null);
        assertThat(devisDTO1).isNotEqualTo(devisDTO2);
    }
}
