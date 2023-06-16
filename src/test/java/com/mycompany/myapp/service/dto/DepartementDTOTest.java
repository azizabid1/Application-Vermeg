package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DepartementDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DepartementDTO.class);
        DepartementDTO departementDTO1 = new DepartementDTO();
        departementDTO1.setId(1L);
        DepartementDTO departementDTO2 = new DepartementDTO();
        assertThat(departementDTO1).isNotEqualTo(departementDTO2);
        departementDTO2.setId(departementDTO1.getId());
        assertThat(departementDTO1).isEqualTo(departementDTO2);
        departementDTO2.setId(2L);
        assertThat(departementDTO1).isNotEqualTo(departementDTO2);
        departementDTO1.setId(null);
        assertThat(departementDTO1).isNotEqualTo(departementDTO2);
    }
}
