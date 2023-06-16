package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DepartementMapperTest {

    private DepartementMapper departementMapper;

    @BeforeEach
    public void setUp() {
        departementMapper = new DepartementMapperImpl();
    }
}
