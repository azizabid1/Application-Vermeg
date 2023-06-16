package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StatusEmployeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StatusEmploye.class);
        StatusEmploye statusEmploye1 = new StatusEmploye();
        statusEmploye1.setId(1L);
        StatusEmploye statusEmploye2 = new StatusEmploye();
        statusEmploye2.setId(statusEmploye1.getId());
        assertThat(statusEmploye1).isEqualTo(statusEmploye2);
        statusEmploye2.setId(2L);
        assertThat(statusEmploye1).isNotEqualTo(statusEmploye2);
        statusEmploye1.setId(null);
        assertThat(statusEmploye1).isNotEqualTo(statusEmploye2);
    }
}
