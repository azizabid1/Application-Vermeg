package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StatusEmployeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StatusEmployeDTO.class);
        StatusEmployeDTO statusEmployeDTO1 = new StatusEmployeDTO();
        statusEmployeDTO1.setId(1L);
        StatusEmployeDTO statusEmployeDTO2 = new StatusEmployeDTO();
        assertThat(statusEmployeDTO1).isNotEqualTo(statusEmployeDTO2);
        statusEmployeDTO2.setId(statusEmployeDTO1.getId());
        assertThat(statusEmployeDTO1).isEqualTo(statusEmployeDTO2);
        statusEmployeDTO2.setId(2L);
        assertThat(statusEmployeDTO1).isNotEqualTo(statusEmployeDTO2);
        statusEmployeDTO1.setId(null);
        assertThat(statusEmployeDTO1).isNotEqualTo(statusEmployeDTO2);
    }
}
