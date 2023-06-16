package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StatusEmployeMapperTest {

    private StatusEmployeMapper statusEmployeMapper;

    @BeforeEach
    public void setUp() {
        statusEmployeMapper = new StatusEmployeMapperImpl();
    }
}
